(defproject {{name}} "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :global-vars {*warn-on-reflection* true}
  :plugins [[lein-servlet "0.4.0"]]
  :dependencies [[org.clojure/clojure "1.5.1"]]
  :aot [{{name}}.servlet]
  :servlet {;; IMPORTANT: only any one of the below :deps entries may be uncommented
            ;; :deps    [[lein-servlet/adapter-jetty7  "0.4.0"]]
            ;; :deps    [[lein-servlet/adapter-jetty8  "0.4.0"]]
            ;; :deps    [[lein-servlet/adapter-jetty9  "0.4.0"]]
            :deps    [[lein-servlet/adapter-tomcat7 "0.4.0"]]
            ;; :deps    [[lein-servlet/adapter-tomcat8 "0.4.0"]]
            :config  {:port 3000}
            :webapps {"/" {:servlets {"/*" '{{sanitized}}.servlet}
                           :public "public"}}})
