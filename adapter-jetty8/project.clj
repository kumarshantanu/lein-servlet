(defproject lein-servlet/adapter-jetty8 "0.2.0"
  :description "Jetty 8 adapter for lein-servlet"
  :url "https://github.com/kumarshantanu/lein-servlet"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :eval-in-leiningen true
  :dependencies [[org.eclipse.jetty/jetty-server  "8.1.4.v20120524"]
                 [org.eclipse.jetty/jetty-servlet "8.1.4.v20120524"]
                 [org.eclipse.jetty/jetty-webapp  "8.1.4.v20120524"]
                 [org.eclipse.jetty/jetty-jsp     "8.1.4.v20120524"]
                 [org.eclipse.jetty/jetty-jndi    "8.1.4.v20120524"]
                 [lein-servlet/adapter-jetty7     "0.2.0"
                  :exclusions [org.eclipse.jetty/jetty-server
                               org.eclipse.jetty/jetty-servlet
                               org.eclipse.jetty/jetty-webapp
                               org.eclipse.jetty/jetty-jsp
                               org.eclipse.jetty/jetty-jndi]]])

