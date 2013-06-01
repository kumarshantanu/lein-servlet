(ns leiningen.servlet.engine.jetty
  "Jetty adapter plugin for lein-servlet"
  (:import (java.net                         InetSocketAddress)
           (javax.servlet.http               HttpServlet)
           (org.eclipse.jetty.server         Connector Handler Server ServerConnector)
           (org.eclipse.jetty.server.handler ContextHandlerCollection)
           (org.eclipse.jetty.servlet        ServletContextHandler ServletHolder)
           (org.eclipse.jetty.util.ssl       SslContextFactory)
           (org.eclipse.jetty.webapp         WebAppContext)))


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


(defn make-servlet-instance
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
  [context-path servlets-config public-dir app-config]
  (let [context (ServletContextHandler. ServletContextHandler/SESSIONS)]
    (doto context
      (.setContextPath  (as-context-path context-path))
      (.setResourceBase public-dir)
      (.setClassLoader  (-> (Thread/currentThread) .getContextClassLoader)))
    (doseq [[url-pattern servlet-info] servlets-config]
      (assert (string? url-pattern))
      (let [[servlet-class
             init-params]  (as-servlet-config servlet-info)
            servlet-holder (-> (make-servlet-instance servlet-class)
                               (ServletHolder.))]
        ;; set servlet init params if specified
        (when (seq init-params)
          (assert (map? init-params))
          (doseq [[param-name param-value] init-params]
            (.setInitParameter servlet-holder
                               (as-str param-name) (as-str param-value))))
        (.addServlet context servlet-holder url-pattern)))
    context))


(defn make-webapp-context
  [context-path web-xml public-dir app-config]
  (let [context (WebAppContext.)]
    (doto context
      (.setContextPath  (as-context-path context-path))
      (.setResourceBase public-dir)
      (.setClassLoader  (-> (Thread/currentThread) .getContextClassLoader))
      (.setDescriptor   web-xml))
    ;; Refer the URL below for the subsequent options:
    ;; http://wiki.eclipse.org/Jetty/Reference/Jetty_Classloading
    (when (contains? app-config :parent-loader-priority?)
      (let [plp (:parent-loader-priority? app-config)]
        (assert (or (true? plp) (false? plp)))
        (.setParentLoaderPriority context plp)))
    (when (contains? app-config :system-classes)
      (let [sc (:system-classes app-config)]
        (assert (coll? sc))
        (.setSystemClasses context (into-array String (map as-str sc)))))
    (when (contains? app-config :server-classes)
      (let [sc (:server-classes app-config)]
        (assert (coll? sc))
        (.setServerClasses context (into-array String (map as-str sc)))))
    context))


(defn make-ssl-connector
  "Create ServerConnector instance from given SSL configuration and return it.
  See also:
  1. http://www.eclipse.org/jetty/documentation/current/embedding-jetty.html#d0e10866
  2. http://wiki.eclipse.org/Jetty/Howto/Configure_SSL"
  [^Server server ssl-cfg]
  (assert (contains? ssl-cfg :port))
  (assert (pos? (:port ssl-cfg)))
  (let [ctx-factory (SslContextFactory.)
        set-str-arg (fn [k f] (when (contains? ssl-cfg k)
                               (let [v (get ssl-cfg k)]
                                 (assert (string? v)) (f v))))]
    (set-str-arg :keystore-path       #(.setKeyStorePath       ctx-factory %))
    (set-str-arg :keystore-password   #(.setKeyStorePassword   ctx-factory %))
    (set-str-arg :keymanager-password #(.setKeyManagerPassword ctx-factory %))
    (set-str-arg :truststore-path     #(.setTrustStore         ctx-factory %))
    (set-str-arg :truststore-password #(.setTrustStorePassword ctx-factory %))
    (let [connector (ServerConnector. server ^SslContextFactory ctx-factory)]
      (.setPort connector (:port ssl-cfg))
      (.setIdleTimeout connector (or (:max-idle-time ssl-cfg) 30000))
      connector)))


(defn jetty
  "Start Jetty server with given configuration"
  [config webapps]
  (let [sockaddr (InetSocketAddress. (:host config) (:port config))
        server   (Server. sockaddr)
        contexts (ContextHandlerCollection.)]
    ;; set HTTP (and if specified, SSL) connectors
    (->> (when (contains? config :ssl)  ;; SSL connector
           (let [ssl-cfg (:ssl config)]
             (assert (map? ssl-cfg))
             (make-ssl-connector server ssl-cfg)))
         (vector (doto (ServerConnector. server)  ;; HTTP connector
                   (.setPort (:port config))))
         (remove nil?)
         (into-array Connector)
         (.setConnectors server))
    ;; prepare webapps
    (->> (for [[ctx-path webapp-cfg] webapps]
           (let [pub (:public webapp-cfg)
                 cfg (merge config (:config webapp-cfg))]
             (if (contains? webapp-cfg :servlets)
               (make-servlet-context ctx-path (:servlets webapp-cfg) pub cfg)
               (make-webapp-context  ctx-path (:web-xml webapp-cfg) pub cfg))))
         (into-array Handler)
         (.setHandlers contexts))
    (.setHandler server contexts)
    ;; start server
    (.start server)
    server))


(defn join
  [^Server server]
  (.join server))