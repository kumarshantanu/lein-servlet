(ns leiningen.new.lein-servlet
  (:require [clojure.string :as str]
            [leiningen.new.lein-servlet.flavor.quercus :as quercus]
            [leiningen.new.lein-servlet.flavor.railo   :as railo]
            [leiningen.new.lein-servlet.flavor.struts  :as struts])
  (:use leiningen.new.templates))


(def render (renderer "lein_servlet"))


(defn no-such-template
  [flavor]
  (println "No such template flavor:" flavor
           "-- valid choices are: quercus, railo, struts"))


(defn lein-servlet
  "A skeleton project to use with lein-servlet"
  ([name]
     (let [data {:year (+ 1900 (.getYear (java.util.Date.)))
                 :name name
                 :sanitized (sanitize name)}]
       (println "Generating a skeleton Clojure/servlet project"
                (str name "..."))
       (->files data
                ["project.clj" (render "project.clj" data)]
                ["README.md"   (render "README.md"   data)]
                ["src/{{sanitized}}/core.clj"       (render "core.clj" data)]
                ["src/{{sanitized}}/servlet.clj"    (render "servlet.clj" data)]
                ["test/{{sanitized}}/core_test.clj" (render "core_test.clj" data)]
                "public/WEB-INF"
                "public/css"
                "public/js"
                "public/img")))
  ([flavor name]
     (case (str/lower-case flavor)
       "quercus"    (quercus/quercus name)
       "railo"      (railo/railo     name)
       "struts"     (struts/struts   name)
       (no-such-template flavor))))
