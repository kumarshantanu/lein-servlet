(ns leiningen.servlet.util
  "Utility functions"
  (:require [clojure.string :as str]))


(defn echo
  ([x]
     (echo nil x))
  ([msg x]
     (println "[ECHO]" msg (pr-str x)) (flush) ;; comment out to turn off msgs
     x))


(defn as-str
  "Turn anything into string"
  [x]
  (if (some #(% x) [symbol? keyword?]) (name x)
      (str x)))


(defn err-println
  "`println` for STDERR; returns 1 as result"
  [& args]
  (binding [*out* *err*]
    (apply println "ERROR:" args)
    1))


(defn comma-sep
  "Given a collection, return comma separated string composed of all elements"
  [coll]
  (str/join ", " coll))