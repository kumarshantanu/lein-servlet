(ns leiningen.new.lein-servlet.flavor.struts
  (:use leiningen.new.templates))

(def render (renderer "lein_servlet/flavor/struts"))

(defn struts
  "A skeleton project for Struts (Java) web development; use with lein-servlet"
  [name]
  (let [data {:year (+ 1900 (.getYear (java.util.Date.)))
              :name name
              :sanitized (sanitize name)}]
    (println "Generating a skeleton project for Struts (Java) development"
             (str name "..."))
    (println (format "Once the project is created, execute this at command-line:
    $ cd %s
    $ lein servlet run" name))
    (let [r #(render % data)
          j (partial str "java-src/{{sanitized}}/")
          p (partial str "public/")
          w (partial str "public/WEB-INF/")]
      (->files data
               ["project.clj"                         (r "project.clj.tpl")]
               ["README.md"                           (r "README.md")]
               ["Struts.README.txt"                   (r "Struts.README.txt")]
               ["Struts.LICENSE.txt"                  (r "Struts.LICENSE.txt")]
               [(j "ApplicationResource.properties")  (r "ApplicationResource.properties")]
               [(j "HelloWorldAction.java")           (r "HelloWorldAction.java")]
               [(j "HelloWorldActionForm.java")       (r "HelloWorldActionForm.java")]
               [(j "util/Clojure.java")               (r "Clojure.java")]
               ["src/{{sanitized}}/core.clj"          (r "core.clj")]
               ["test/{{sanitized}}/core_test.clj"    (r "core_test.clj")]
               [(p "index.jsp")                       (r "index.jsp")]
               [(p "helloWorld.jsp")                  (r "helloWorld.jsp")]
               [(w "struts-bean.tld")                 (r "struts_bean.tld")]
               [(w "struts-html.tld")                 (r "struts_html.tld")]
               [(w "struts-nested.tld")               (r "struts_nested.tld")]
               [(w "tiles-defs.xml")                  (r "tiles_defs.xml")]
               [(w "validator-rules.xml")             (r "validator_rules.xml")]
               [(w "struts-config.xml")               (r "struts_config.xml")]
               [(w "struts-logic.tld")                (r "struts_logic.tld")]
               [(w "struts-tiles.tld")                (r "struts_tiles.tld")]
               [(w "validation.xml")                  (r "validation.xml")]
               [(w "web.xml")                         (r "web.xml")]
               (p "css")
               (p "js")
               (p "img")))))
