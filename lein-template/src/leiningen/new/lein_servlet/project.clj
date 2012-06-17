(defproject {{name}} "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :warn-on-reflection true
  :plugins [[lein-servlet "0.1.0"]]
  :dependencies [[org.clojure/clojure "1.4.0"]]
  :aot [{{name}}.servlet]
  :servlet {;; uncomment only either of the :deps entries below
            ;; :deps    [[lein-servlet/adapter-jetty  "0.1.0"]]
            ;; :deps    [[lein-servlet/adapter-jett8  "0.1.0"]]
            :deps    [[lein-servlet/adapter-tomcat7 "0.1.0"]]
            :config  {:port 3000}
            :webapps {"/" {:classes {"/*" '{{sanitized}}.servlet}
                           :public "public"}}})
