(defproject {{name}} "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Apache License, Version 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0.html"}
  :warn-on-reflection true
  :plugins [[lein-servlet "0.1.0"]]
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [struts              "1.2.9"]]
  :java-source-paths ["java-src"]
  :aot [{{name}}.util.jclojure]
  :servlet {;; uncomment only either of the :deps entries below
            ;; :deps    [[lein-servlet/adapter-jetty7  "0.1.0"]]
            ;; :deps    [[lein-servlet/adapter-jetty8  "0.1.0"]]
            :deps    [[lein-servlet/adapter-tomcat7 "0.1.0"]]
            :config  {:port 3000}
            :webapps {"/" {:web-xml "public/WEB-INF/web.xml"
                           :public  "public"
                           :config  {:parent-loader-priority? true}}}})
