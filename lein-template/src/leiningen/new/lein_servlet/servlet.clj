(ns {{name}}.servlet
  (:import (java.io            PrintWriter)
           (javax.servlet      ServletConfig)
           (javax.servlet.http HttpServletRequest HttpServletResponse))
  (:gen-class :extends javax.servlet.http.HttpServlet))


(defn -init
  ([this]
   (println "Servlet initialized with no params"))
  ([this ^ServletConfig config]
   (println "Servlet initialized with servlet config" config)))


(defn handle
  [^HttpServletRequest request ^HttpServletResponse response]
  (let [template "<html>
  <head>
    <title>{{name}}</title>
  </head>
  <body>
    <br/>
    <h1>{{name}}</h1>
    <big>
        <big>
          <br/>
          Welcome! Your Clojure/servlet project is created.<br/><br/>
        </big>

        You should modify the generated content to continue hacking on the project.<br/><br/>
      </big>

  </body>
</html>
"]
    (.setContentType response "text/html")
    (doto ^PrintWriter (.getWriter response)
      (.println template))))


(defn -doGet
  [this ^HttpServletRequest request ^HttpServletResponse response]
  (handle request response))


(defn -doPost
  [this ^HttpServletRequest request ^HttpServletResponse response]
  (handle request response))


(defn -destroy
  [this]
  (println "Servlet destroyed"))

