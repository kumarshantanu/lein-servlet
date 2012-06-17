(ns leiningen.new.lein-servlet.flavor.quercus
  (:use leiningen.new.templates))

(def render (renderer "lein_servlet/flavor/quercus"))

(defn quercus
  "A skeleton project for PHP (Quercus) development; use with lein-servlet"
  [name]
  (let [data {:year (+ 1900 (.getYear (java.util.Date.)))
              :name name
              :sanitized (sanitize name)}]
    (println "Generating a skeleton project for PHP (Quercus) development"
             (str name "..."))
    (println (format "Once the project is created, execute this at command-line:
    $ cd %s
    $ lein servlet run" name))
    (let [r #(render % data)]
      (->files data
               ["project.clj"                         (r "project.clj.tpl")]
               ["README.md"                           (r "README.md")]
               ["Quercus.README.txt"                  (r "Quercus.README.txt")]
               ["Quercus.LICENSE.txt"                 (r "Quercus.LICENSE.txt")]
               ["src/{{sanitized}}/core.clj"          (r "core.clj")]
               ["src/{{sanitized}}/util/jclojure.clj" (r "jclojure.clj")]
               ["test/{{sanitized}}/core_test.clj"    (r "core_test.clj")]
               ["public/WEB-INF/web.xml"              (r "web.xml")]
               ["public/index.php"                    (r "index.php")]
               "public/css"
               "public/js"
               "public/img"))))
