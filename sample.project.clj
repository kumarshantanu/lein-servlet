;; This is an annotated example of the options that may be set in the
;; :servlet map of a project.clj file. It is a fairly contrived example
;; in order to cover all options exhaustively.
(defproject org.example/sample "0.1.0-SNAPSHOT"
  ;; ----- other entries omitted -----
  ;; Project level usage of the lein-servlet plugin is recommended
  :plugins [[lein-servlet "0.1.0"]]
  ;; Configuration for the :lein-servlet plugin (required)
  :servlet {;;; (required) At most one engine adapter should be specified here
            :deps [[lein-servlet/adapter-jetty "0.1.0"]]
            ;;; (optional) Map of engine attributes
            :config {;; (optional) Unless specified, first available engine in
                     ;; classpath is used. Useful in rare cases when more than
                     ;; one engine is on classpath; to disambiguate.
                     :engine :jetty
                     ;; (optional) Default host is "localhost"
                     :host   "0.0.0.0"
                     ;; (optional) Default port is 3000
                     :port   3000
                     ;; (optional) Default is "<target-path>/<eng-name>.<port>"
                     :tmpdir "target/engine"
                     ;; (optional) SSL will be configured only when specified
                     :ssl    {;; (optional) default is 3443
                              :port 3443
                              ;; (optional) Default is "$HOME/.keystore"
                              :keystore-path       "/home/joe/.keystore"
                              ;; (required) Password for your keystore
                              :keystore-password   "s3cr3t"
                              ;; FIXME
                              :keymanager-password ""
                              ;; FIXME
                              :truststore-path     ""
                              ;; FIXME
                              :truststore-password ""}
                     ;; (optional) TODO
                     :datasource {}}
            ;;; (required) Non-empty map of context-path to webapp-config
            :webapps {;; (required) Context-path - turns into '/app1'
                      :app1
                      {;; Map of URL-patterns to servlet classes.
                       ;; Every webapp must have either :classes or :web-xml
                       :classes {"/*"      com.myapp.WebappServlet
                                 "/fr/*"   [com.myapp.WebappServlet {:locale "fr"}]
                                 "/it/*"   [com.myapp.WebappServlet {:locale "it"}]
                                 "/auth/*" com.myapp.AuthServlet {:method "SamlToken"
                                                                  :retries 5}}
                       ;; (required) Directory location for public resources
                       :public  "public"}}
                     {;; (required) Context-path of the webapp
                      "/app2"
                      {;; Path to the `web.xml` file
                       ;; Every webapp must have either :classes or :web-xml
                       :web-xml "public/WEB-INF/web.xml"
                       ;; (required) Directory location for public resources
                       :public  "public"
                       ;;; (optional) Per webapp engine-specific config
                       :config {:parent-loader-priority? true
                                :system-classes []
                                :server-classes []}}}})
