(defproject {{name}} "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "GNU General Public License 2.0"
            :url "http://www.gnu.org/licenses/gpl-2.0.html"}
  :warn-on-reflection true
  :plugins [[lein-servlet "0.1.0"]]
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [com.caucho/resin    "4.0.28"]]
  :repositories {"caucho" "http://caucho.com/m2"}
  :aot [{{name}}.util.jclojure]
  :servlet {;; uncomment only either of the :deps entries below
            ;; :deps    [[lein-servlet/adapter-jetty7  "0.1.0"]]
            ;; :deps    [[lein-servlet/adapter-jetty8  "0.1.0"]]
            :deps    [[lein-servlet/adapter-tomcat7 "0.1.0"]]
            ;; :repos   {"caucho" "http://caucho.com/m2"}
            :config  {:port 3000}
            :webapps {"/" {:web-xml "public/WEB-INF/web.xml"
                           :public  "public"
                           :config  {:parent-loader-priority? true}}}})
