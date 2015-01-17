(defproject lein-servlet/adapter-jetty9 "0.4.1"
  :description "Jetty 9 adapter for lein-servlet"
  :url "https://github.com/kumarshantanu/lein-servlet"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :eval-in-leiningen true
  :dependencies [[org.eclipse.jetty/jetty-server  "9.2.6.v20141205"]
                 [org.eclipse.jetty/jetty-servlet "9.2.6.v20141205"]
                 [org.eclipse.jetty/jetty-webapp  "9.2.6.v20141205"]
                 [org.eclipse.jetty/jetty-jsp     "9.2.6.v20141205"]
                 [org.eclipse.jetty/jetty-jndi    "9.2.6.v20141205"]])
