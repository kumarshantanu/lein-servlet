(ns leiningen.new.lein-servlet.flavor.railo
  (:use leiningen.new.templates))

(def render (renderer "lein_servlet/flavor/railo"))

(defn railo
  "A skeleton project for CFML (Railo) development; use with lein-servlet"
  [name]
  (let [data {:year (+ 1900 (.getYear (java.util.Date.)))
              :name name
              :sanitized (sanitize name)}]
    (println "Generating a skeleton project for CFML (Railo) development"
             (str name "..."))
    (println (format "Once the project is created, execute this at command-line:
    $ cd %s
    $ lein servlet run" name))
    (let [r #(render % data)]
      (->files data
               ["project.clj"                         (r "project.clj.tpl")]
               ["README.md"                           (r "README.md")]
               ["Railo.README.txt"                    (r "Railo.README.txt")]
               ["Railo.LICENSE.txt"                   (r "Railo.LICENSE.txt")]
               ["src/{{sanitized}}/core.clj"          (r "core.clj")]
               ["src/{{sanitized}}/util/jclojure.clj" (r "jclojure.clj")]
               ["test/{{sanitized}}/core_test.clj"    (r "core_test.clj")]
               ["public/WEB-INF/web.xml"              (r "web.xml")]
               ["public/Application.cfc"              (r "Application.cfc")]
               ["public/index.cfm"                    (r "index.cfm")]
               "public/css"
               "public/js"
               "public/img"))))
