(ns leiningen.servlet.engine.tomcat
  "See URL:
http://people.apache.org/~markt/presentations/2010-11-04-Embedding-Tomcat.pdf"
  (:import (java.io File)
           (javax.servlet.http            HttpServlet)
           (org.apache.catalina           Context Wrapper)
           (org.apache.catalina.connector Connector)
           (org.apache.catalina.startup   Tomcat)))


(defn as-str
  "Convert `x` (anything) to string"
  [x]
  (if (or (keyword? x) (symbol? x)) (name x)
      (str x)))


(defn as-context-path
  [ctx]
  (cond (keyword? ctx) (str "/" (name ctx))
        (symbol?  ctx) (str "/" (name ctx))
        (string?  ctx) ctx
        :otherwise     (throw (IllegalArgumentException.
                               (str "Expected string/keyword/symbol but found "
                                    (type ctx) " " (pr-str ctx))))))


(defn as-class
  [x]
  (if (class? x) x
      (Class/forName (as-str x))))


(defn ^HttpServlet make-servlet-instance
  "Return servlet instance"
  [servlet-class]
  (cond (instance? HttpServlet servlet-class)
        servlet-class
        ;;;
        (class? servlet-class)
        (.newInstance servlet-class)
        :otherwise
        (make-servlet-instance (as-class servlet-class))))


(defn as-servlet-config
  [servlet-config]
  (if (coll? servlet-config) (into [] servlet-config)
      [servlet-config {}]))


(defn make-servlet-context
  [^Tomcat tomcat context-path servlets-config public-dir app-config]
  (let [^Context
        context (.addContext tomcat
                             (as-context-path context-path)
                             (-> public-dir (File.) (.getAbsolutePath)))]
    (doseq [[url-pattern servlet-info] servlets-config]
      (assert (string? url-pattern))
      (let [^String  servlet-name    (str (gensym))
                    [servlet-class
                     init-params]    (as-servlet-config servlet-info)
            ^Wrapper servlet-wrapper (->> (make-servlet-instance servlet-class)
                                          (Tomcat/addServlet context servlet-name))]
        ;;(.addServlet tomcat url-pattern servlet-name (make-servlet-instance servlet-class))
        ;; set servlet init params if specified
        (when (seq init-params)
          (doseq [[param-name param-value] init-params]
            (.addInitParameter servlet-wrapper
                               (as-str param-name) (as-str param-value))))
        (.addServletMapping context url-pattern servlet-name)))
    context))


(defn make-webapp-context
  [tomcat context-path web-xml public-dir app-config]
  (let [context (.addWebapp tomcat (as-context-path context-path)
                            (-> public-dir (File.) (.getAbsolutePath)))]
    context))


(defn make-ssl-connector
  "Create SslSelectChannelConnector instance and return it.
  See also:
  1. http://www.copperykeenclaws.com/adding-an-https-connector-to-embedded-tomcat-7/"
  [ssl-cfg]
  (println "SSL config:" ssl-cfg)
  (assert (contains? ssl-cfg :port))
  (assert (pos? (:port ssl-cfg)))
  (let [ssl-cctr (doto (Connector.)
                   (.setPort (:port ssl-cfg))
                   (.setSecure true)
                   (.setScheme "https")
                   (.setAttribute "clientAuth" false)
                   (.setAttribute "sslProtocol" "TLS")
                   (.setAttribute "SSLEnabled"  true))
        set-attr (fn [k a] (when (contains? ssl-cfg k)
                            (let [v (get ssl-cfg k)]
                              (assert (not (nil? v)))
                              (.setAttribute ssl-cctr a v))))]
    (set-attr :key-alias         "keyAlias")
    (set-attr :keystore-path     "keystoreFile")
    (set-attr :keystore-password "keystorePass")
    ssl-cctr))


(defn tomcat
  "Start Tomcat server with given configuration"
  [config webapps]
  (let [tomcat  (doto (Tomcat.)
                  (.setBaseDir  (:tmpdir config))
                  (.setHostname (:host config))
                  (.setPort     (:port config)))
        service (.getService tomcat)]
    ;; set additional SSL connector is specified in configuration
    (when (contains? config :ssl)
      (let [ssl-cfg (:ssl config)]
        (assert (map? ssl-cfg))
        (.addConnector service (make-ssl-connector ssl-cfg))))
    ;; prepare webapps
    (doseq [[ctx-path webapp-cfg] webapps]
      (let [pub (:public webapp-cfg)
            cfg (merge config (:config webapp-cfg))]
        (if (contains? webapp-cfg :servlets)
          (make-servlet-context tomcat ctx-path (:servlets webapp-cfg) pub cfg)
          (make-webapp-context  tomcat ctx-path (:web-xml webapp-cfg) pub cfg))))
    ;; start server
    ;; (.enableNaming tomcat)
    (.start tomcat)
    tomcat))


(defn join
  [server]
  (while true
    (try (Thread/sleep 5000)
         (catch InterruptedException e
           (.interrupt (Thread/currentThread))))))
