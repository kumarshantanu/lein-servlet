(defproject {{name}} "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "GNU Lesser General Public License 2.1"
            :url "http://www.gnu.org/licenses/lgpl-2.1.html"}
  :global-vars {*warn-on-reflection* true}
  :plugins [[lein-servlet "0.4.0"]]
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.getrailo/railo "4.3.0.000" :exclusions [[javax.servlet/servlet-api]] ]]
  :repositories {"cfmlprojects.org" "http://cfmlprojects.org/artifacts"
                 "railo-repo" "http://railo-repo.sourceforge.net/maven2"}
  :aot [{{name}}.util.jclojure]
  :servlet {;; uncomment only either of :deps entries below
            ;; :deps    [[lein-servlet/adapter-jetty7  "0.4.0"]]
            ;; :deps    [[lein-servlet/adapter-jetty8  "0.4.0"]]
            ;; :deps    [[lein-servlet/adapter-jetty9  "0.4.0"]]
            :deps    [[lein-servlet/adapter-tomcat7 "0.4.0"]]
            ;; :deps    [[lein-servlet/adapter-tomcat8 "0.4.0"]]
            :config  {:port 3000}
            :webapps {"/" {:web-xml "public/WEB-INF/web.xml"
                           :public  "public"
                           :config  {:parent-loader-priority? true}}}})