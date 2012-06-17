;; This is an annotated example of the options that may be set in the
;; :servlet map of a project.clj file. It is a fairly contrived example
;; in order to cover all options exhaustively.
(defproject org.example/sample "0.1.0-SNAPSHOT"
  ;; ----- other entries omitted -----
  ;; Project level usage of the lein-servlet plugin is recommended
  :plugins [[lein-servlet "0.1.0"]]
  ;; Configuration for the :lein-servlet plugin (required)
  :servlet {;;; (required) engine adapters should be specified here
            :deps [[lein-servlet/adapter-jetty "0.1.0"]]
            ;;; (optional) map of engine attributes
            :config {;; (optional) unless specified, first available is used
                     :engine :jetty
                     ;; (optional) default host is "localhost"
                     :host   "0.0.0.0"
                     ;; (optional) default port is 3000
                     :port   3000
                     ;; (optional) default is "<target-path>/<eng-name>.<port>"
                     :tmpdir "target/engine"
                     ;; (optional) SSL will be configured only when specified
                     :ssl    {;; (optional) default is 3443
                              :port 3443
                              ;; (optional) default is "$HOME/.keystore"
                              :keystore-path       "/home/joe/.keystore"
                              ;; (required) password for your keystore
                              :keystore-password   "s3cr3t"
                              ;; FIXME
                              :keymanager-password ""
                              ;; FIXME
                              :truststore-path     ""
                              ;; FIXME
                              :truststore-password ""}
                     ;; (optional) TODO
                     :datasource {}}
            ;;; (required) non-empty map of context-path to webapp-config
            :webapps {;; (required) context-path - turns into '/app1'
                      :app1
                      {;; map of URL-patterns to servlet classes
                       ;; every webapp must have either :classes or :web-xml
                       :classes {"/*"      com.myapp.WebappServlet
                                 "/fr/*"   [com.myapp.WebappServlet "fr"]
                                 "/it/*"   [com.myapp.WebappServlet "it"]
                                 "/auth/*" com.myapp.AuthServlet}
                       ;; (required) directory location for public resources
                       :public  "public"}}
                     {;; (required) context-path of the webapp
                      "/app2"
                      {;; path to the `web.xml` file
                       ;; every webapp must have either :classes or :web-xml
                       :web-xml "public/WEB-INF/web.xml"
                       ;; (required) directory location for public resources
                       :public  "public"
                       ;;; (optional) per webapp engine-specific config
                       :config {:parent-loader-priority? true
                                :system-classes []
                                :server-classes []}}}})
