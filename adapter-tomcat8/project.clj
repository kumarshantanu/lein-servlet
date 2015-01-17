(defproject lein-servlet/adapter-tomcat8 "0.4.1"
  :description "Tomcat 8 adapter for lein-servlet"
  :url "https://github.com/kumarshantanu/lein-servlet"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :eval-in-leiningen true
  :dependencies
  [;; see http://people.apache.org/~markt/presentations/2010-11-04-Embedding-Tomcat.pdf
   ;; required
   [org.apache.tomcat.embed/tomcat-embed-core          "8.0.17"]
   [org.apache.tomcat.embed/tomcat-embed-logging-juli  "8.0.17"]
   [org.apache.tomcat.embed/tomcat-embed-logging-log4j "8.0.17"]
   ;; optional, Connection pooling
   [org.apache.tomcat/tomcat-dbcp               "8.0.17"]
   ;; optional, JSP support
   [org.apache.tomcat.embed/tomcat-embed-jasper "8.0.17"]
   [org.eclipse.jdt.core.compiler/ecj           "4.4"]
   [lein-servlet/adapter-tomcat7 "0.4.1"
    :exclusions [org.apache.tomcat.embed/tomcat-embed-core
                 org.apache.tomcat.embed/tomcat-embed-logging-juli
                 org.apache.tomcat.embed/tomcat-embed-logging-log4j
                 org.apache.tomcat/tomcat-dbcp
                 org.apache.tomcat.embed/tomcat-embed-jasper
                 org.eclipse.jdt.core.compiler/ecj]]])
