(ns clj-time.core-test
  (:refer-clojure :exclude [extend])
  (:use clojure.test)
  (:use clj-time.core))

(deftest test-now
  (is (<= 2010 (year (now)))))

(deftest test-epoch
  (let [e (epoch)]
    (is (= 1970 (year e)))
    (is (= 0 (sec e)))))

(deftest test-date-time-and-accessors
  (let [d (date-time 1986)]
    (is (= 1986 (year   d)))
    (is (= 1    (month  d)))
    (is (= 1    (day    d)))
    (is (= 0    (hour   d)))
    (is (= 0    (minute d)))
    (is (= 0    (sec    d)))
    (is (= 0    (milli  d))))
  (let [d (date-time 1986 10 14 4 3 2 1)]
    (is (= 1986 (year   d)))
    (is (= 10   (month  d)))
    (is (= 14   (day    d)))
    (is (= 4    (hour   d)))
    (is (= 3    (minute d)))
    (is (= 2    (sec    d)))
    (is (= 1    (milli  d)))))

(deftest test-day-of-week
  (let [d (date-time 2010 4 24)]
    (is (= 6 (day-of-week d))))
  (let [d (date-time 1918 11 11)]
    (is (= 1 (day-of-week d)))))

(deftest test-to-time-zone
  (let [tz  (time-zone-for-offset 2)
        dt1 (date-time 1986 10 14 6)
        dt2 (to-time-zone dt1 tz)]
    (is (= 8 (hour dt2)))
    (is (= (.getMillis dt1) (.getMillis dt2)))))

(deftest test-from-time-zone
  (let [tz  (time-zone-for-offset 2)
        dt1 (date-time 1986 10 14 6)
        dt2 (from-time-zone dt1 tz)]
    (is (= 6 (hour dt2)))
    (is (> (.getMillis dt1) (.getMillis dt2)))))

(deftest test-after?
  (is (after? (date-time 1987) (date-time 1986)))
  (is (not (after? (date-time 1986) (date-time 1987))))
  (is (not (after? (date-time 1986) (date-time 1986)))))

(deftest test-before?
  (is (before? (date-time 1986) (date-time 1987)))
  (is (not (before? (date-time 1987) (date-time 1986))))
  (is (not (before? (date-time 1986) (date-time 1986)))))

(deftest test-periods
  (is (= (date-time 1986 10 14 4 3 2 1)
         (plus (date-time 1984)
           (years 2)
           (months 9)
           (days 13)
           (hours 4)
           (minutes 3)
           (secs 2)
           (millis 1))))
  (is (= (date-time 1986 1 8)
         (plus (date-time 1986 1 1) (weeks 1)))))

(deftest test-plus
  (is (= (date-time 1986 10 14 6)
         (plus (date-time 1986 10 14 4) (hours 2))))
  (is (= (date-time 1986 10 14 6 5)
         (plus (date-time 1986 10 14 4 2) (hours 2) (minutes 3)))))

(deftest test-minus
  (is (= (date-time 1986 10 14 4)
         (minus (date-time 1986 10 14 6) (hours 2))))
  (is (= (date-time 1986 10 14 4 2)
         (minus (date-time 1986 10 14 6 5) (hours 2) (minutes 3)))))

(deftest test-ago
  (binding [now #(date-time 2011 4 16 10 9 00)]
    (is (= (-> 10 years ago)
           (date-time 2001 4 16 10 9 00)))))

(deftest test-from-now
  (binding [now #(date-time 2011 4 16 10 9 00)]
    (is (= (-> 30 minutes from-now)
           (date-time 2011 4 16 10 39 00)))))

(deftest test-start-end
  (let [s (date-time 1986 10 14 12 5 4)
        e (date-time 1986 11 3  22 2 6)
        p (interval s e)]
    (is (= s (start p)))
    (is (= e (end p)))))

(deftest test-extend
  (is (= (interval (date-time 1986) (date-time 1988))
         (extend (interval (date-time 1986) (date-time 1987)) (years 1)))))

(deftest test-interval-in
  (let [p (interval (date-time 1986 10 14 12 5 4) (date-time 1986 11 3  22 2 6))]
    (is (= 29397   (in-minutes p)))
    (is (= 1763822 (in-secs p)))
    (is (= 1763822000 (in-msecs p)))))

(deftest test-within?
  (let [d1 (date-time 1985)
        d2 (date-time 1986)
        d3 (date-time 1987)]
    (is (within? (interval d1 d3) d2))
    (is (not (within? (interval d1 d2) d3)))
    (is (not (within? (interval d1 d2) d2)))
    (is (not (within? (interval d2 d3) d1)))))

(deftest test-overlaps?
  (let [d1 (date-time 1985)
        d2 (date-time 1986)
        d3 (date-time 1987)
        d4 (date-time 1988)]
    (is (overlaps? (interval d1 d3) (interval d2 d4)))
    (is (overlaps? (interval d1 d3) (interval d2 d3)))
    (is (not (overlaps? (interval d1 d2) (interval d2 d3))))
    (is (not (overlaps? (interval d1 d2) (interval d3 d4))))))

(deftest test-abuts?
  (let [d1 (date-time 1985)
        d2 (date-time 1986)
        d3 (date-time 1987)
        d4 (date-time 1988)]
    (is (abuts? (interval d1 d2) (interval d2 d3)))
    (is (not (abuts? (interval d1 d2) (interval d3 d4))))
    (is (not (abuts? (interval d1 d3) (interval d2 d3))))
    (is (abuts? (interval d2 d3) (interval d1 d2)))))

(deftest mins-ago-test
  (is (= 5 (mins-ago (minus (now) (minutes 5))))))
