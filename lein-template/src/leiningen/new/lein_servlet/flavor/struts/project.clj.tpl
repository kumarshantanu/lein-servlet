(defproject {{name}} "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Apache License, Version 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0.html"}
  :global-vars {*warn-on-reflection* true}
  :plugins [[lein-servlet "0.4.1"]]
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.apache.struts/struts-core   "1.3.10"]
                 [org.apache.struts/struts-tiles  "1.3.10"]
                 [org.apache.struts/struts-taglib "1.3.10"]]
  :java-source-paths ["java-src"]
  :servlet {;; uncomment only either of the :deps entries below
            ;; :deps    [[lein-servlet/adapter-jetty7  "0.4.1"]]
            ;; :deps    [[lein-servlet/adapter-jetty8  "0.4.1"]]
            ;; :deps    [[lein-servlet/adapter-jetty9  "0.4.1"]]
            :deps    [[lein-servlet/adapter-tomcat7 "0.4.1"]]
            ;; :deps    [[lein-servlet/adapter-tomcat8 "0.4.1"]]
            :config  {:port 3000}
            :webapps {"/" {:web-xml "public/WEB-INF/web.xml"
                           :public  "public"
                           :config  {:parent-loader-priority? true}}}})
