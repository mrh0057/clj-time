(ns clj-time.coerce
  "Utilites to coerce Joda DateTime instances to and from various other types.
   For example, to convert a Joda DateTime to and from a Java long:
   
     => (to-long (date-time 1998 4 25))
     893462400000
     
     => (from-long 893462400000)
     #<DateTime 1998-04-25T00:00:00.000Z>"
  (:refer-clojure :exclude [extend])
  (:use clj-time.core)
  (:require [clj-time.format :as time-fmt])
  (:import (org.joda.time DateTime DateTimeZone))
  (:import java.util.Date))

(defn to-long
  "Returns the number of milliseconds that the given DateTime is after Unix
   epoch."
  [#^DateTime dt]
  (.getMillis dt))

(defn from-long
  "Returns a DateTime instance in the UTC time zone corresponding to the given
   number of milliseconds after the Unix epoch."
   [#^Long millis]
   (DateTime. millis #^DateTimeZone (. DateTimeZone getDefault)))

(defn from-string
  "return DateTime instance from string using
   formatters in clj-time.format, returning first
   which parses"
  [s]
  (first
       (for [f (vals time-fmt/formatters)
             :let [d (try (time-fmt/parse f s) (catch Exception _ nil))]
             :when d] d)))

(defn to-date
  "Returns a Java Date object corresponding to the given DateTime instance."
  [#^DateTime dt]
  (Date. #^Long (to-long dt)))

(defn from-date
  "Returns a DateTime instance in the UTC time zone corresponding to the given
   Java Date object."
  [#^Date date]
  (from-long (.getTime date)))

(defn to-string
  "Returns a string representation of date in UTC time-zone using
   (ISODateTimeFormat/dateTime) date-time representation. "
  [#^DateTime dt]
  (time-fmt/unparse (:date-time time-fmt/formatters) dt))
