(defproject {{name}} "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "GNU Lesser General Public License 2.1"
            :url "http://www.gnu.org/licenses/lgpl-2.1.html"}
  :warn-on-reflection true
  :plugins [[lein-servlet "0.2.0"]]
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.getrailo/railo "3.3.1.000" :exclusions [[javax.servlet/servlet-api]] ]]
  :repositories {"railo-repo" "http://railo-repo.sourceforge.net/maven2"}
  :aot [{{name}}.util.jclojure]
  :servlet {;; uncomment only either of :deps entries below
            ;; :deps    [[lein-servlet/adapter-jetty7  "0.2.0"]]
            ;; :deps    [[lein-servlet/adapter-jetty8  "0.2.0"]]
            :deps    [[lein-servlet/adapter-tomcat7 "0.2.0"]]
            :config  {:port 3000}
            :webapps {"/" {:web-xml "public/WEB-INF/web.xml"
                           :public  "public"
                           :config  {:parent-loader-priority? true}}}})
