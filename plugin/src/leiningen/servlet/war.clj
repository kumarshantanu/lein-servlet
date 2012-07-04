(ns leiningen.servlet.war
  "Supporting code for `war` sub command"
  (:require [clojure.java.io :as io]
            [clojure.pprint  :as pp]
            [clojure.string  :as str]
            [leiningen.compile        :as lc]
            [leiningen.core.classpath :as classpath]
            [leiningen.core.main      :as main]
            [leiningen.servlet.util   :as util])
  (:import (java.io       BufferedOutputStream ByteArrayInputStream
                          File FileOutputStream)
           (java.util.jar JarEntry JarOutputStream Manifest)
           (java.util.zip ZipException)
           (org.sonatype.aether.resolution DependencyResolutionException)))


(defn classpath-entries
  [project]
  (try (classpath/get-classpath project)
       (catch DependencyResolutionException e
         (main/abort (.getMessage e)))))


(defn new-jar-file
  [path]
  (let [^File ensure-exist #(let [^File f (File. %)]
                                (when-not (.exists f)
                                  (.mkdirs (.getParentFile f))
                                  (.createNewFile f))
                                f)
        ^Manifest manifest (-> "Manifest-Version: 1.0
Created-By: lein-servlet\nBuilt-By: %s\nBuild-Jdk: %s"
                               (format (System/getProperty "user.name")
                                       (System/getProperty "java.version"))
                               (.getBytes)
                               (ByteArrayInputStream.)
                               (Manifest.))]
    (-> path
        ensure-exist
        (FileOutputStream.)
        (BufferedOutputStream.)
        (JarOutputStream. manifest))))


(defn unix-path
  [path] {:pre [(string? path)]}
  (.replace ^String path "\\" "/"))


(defn no-trailing-slash?
  "Return true if `unix-pathname` ends in a slash, false otherwise."
  [unix-path] {:pre [(string? unix-path)]}
  (not= \/ (last unix-path)))


(defn ^String as-jar-dirname
  [jar-dirname]
  (let [path (unix-path jar-dirname)]
    (str path (when (and (not (empty? path)) (no-trailing-slash? path)) \/))))


(defn ^JarEntry jar-entry-dir
  [entry] {:pre [(string? entry) (seq entry)]
           :post [(instance? JarEntry %)]}
  (-> entry as-jar-dirname (JarEntry.)))


(defn ^JarEntry jar-entry-file
  [entry] {:pre [(string? entry) (seq entry)]
           :post [(instance? JarEntry %)]}
  (let [path (unix-path entry)]
    (assert (no-trailing-slash? path))
    (JarEntry. path)))


(defn jar-mkdir!
  "Create directory `jar-path` in JAR"
  [^JarOutputStream jar-out jar-path] {:pre [(instance? JarOutputStream jar-out)]}
  (let [^JarEntry jar-entry (jar-entry-dir jar-path)]
    (doto jar-out
      (.putNextEntry jar-entry)
      (.closeEntry))))


(defn jar-cp!
  "Copy file from `source` to destination `jar-path` in JAR"
  [^JarOutputStream jar-out source jar-path] {:pre [(instance? JarOutputStream jar-out)]}
  (let [^JarEntry jar-entry (jar-entry-file jar-path)]
    (.putNextEntry jar-out jar-entry)
    (when (instance? File source)
      (.setTime jar-entry (.lastModified ^File source)))
    (io/copy source jar-out)
    (.closeEntry jar-out)))


(defn jar-cp-r!
  "Recursively copy files from `source` to destination `jar-path`. Note that
  `jar-path` must exist as a directory in the JAR already."
  [^JarOutputStream jar-out ^File source jar-path] {:pre [(.isDirectory source)]}
  (doseq [^File each-file (.listFiles source)]
    (let [new-jar-path (-> jar-path as-jar-dirname (str (.getName each-file)))]
      (if (.isDirectory each-file)
       (do
         (try (jar-mkdir! jar-out new-jar-path)
              (catch ZipException _)) ;; ignore ZipException on mkdir for overlay
         (jar-cp-r! jar-out each-file new-jar-path))
       (jar-cp! jar-out each-file new-jar-path)))))


(defn emit-servlet-xml
  [ctr url-pattern servlet-class init-params] {:pre [(string? url-pattern)
                                                     (or (string? servlet-class)
                                                         (symbol? servlet-class))
                                                     (map? init-params)]}
  (let [servlet-name (name (gensym))
        init-kv-str (fn [[k v]] (format "
    <init-param>
      <param-name>%s</param-name>
      <param-value>%s</param-value>
    </init-param>"
                                          (util/as-str k) (util/as-str v)))]
    (format "
  <servlet>
    <servlet-name>%s</servlet-name>
    <servlet-class>%s</servlet-class>
    %s
    <load-on-startup>%d</load-on-startup>
  </servlet>
  <servlet-mapping>
    <servlet-name>%s</servlet-name>
    <url-pattern>%s</url-pattern>
  </servlet-mapping>"
            servlet-name (util/as-str servlet-class)
            (->> init-params
                 (map init-kv-str)
                 (str/join "\n"))
            ctr servlet-name url-pattern)))


(defn make-temp-webxml
  [webapp]
  (let [classes   (:classes webapp)
        as-vector #(if (coll? %) (into [] %) [%])
        ^File
        temp-file (File/createTempFile "lein-servlet-" "-web.xml")]
    (assert (map? classes))
    (assert (seq classes))
    (->> classes
         (map (fn [ctr [url-pattern v]]
                (let [[klass init-params] (as-vector v)]
                  (println "klass" klass "init-params" (pr-str init-params)) (flush)
                  (emit-servlet-xml ctr url-pattern klass
                                    (or init-params {})))) (iterate inc 1))
         (str/join "\n")
         (format "<?xml version=\"1.0\" encoding=\"UTF-8\"?>
<!DOCTYPE web-app
     PUBLIC \"-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN\"
    \"http://java.sun.com/dtd/web-app_2_3.dtd\">
<web-app>
  %s
  <session-config>
    <session-timeout>30</session-timeout>
  </session-config>
  <welcome-file-list>
    <welcome-file>index.jsp</welcome-file>
    <welcome-file>index.html</welcome-file>
    <welcome-file>index.htm</welcome-file>
  </welcome-file-list>
</web-app>")
         (spit temp-file))
    temp-file))


(defn generate-war
  "Generate WAR file. Generate a Uberwar (WAR with dependency libraries) when
  `deps?` is true."
  [deps? project webapp]
  (lc/compile (merge-with concat project
                          {:dependencies '[[javax.servlet/servlet-api "2.5"]]}))
  (let [warpath (->> [:target-path :name :version]
                     (map project)
                     (apply format "%s/%s-%s.war"))
        as-vector #(cond (vector? %) %
                         (seq? %)    (into [] %)
                         (nil? %)    []
                         :otherwise  [%])]
    (println "Generating WAR file" warpath)
    (with-open [war-out (new-jar-file warpath)]
      (assert (instance? JarOutputStream war-out))
      ;; all public files
      (let [^File public (File. (:public webapp))]
        (assert (.exists public))
        (assert (.isDirectory public))
        (jar-cp-r! war-out public "")) ;; add public files
      ;; web.xml file
      (let [^File web-xml (io/file (or (:web-xml webapp)
                                       (make-temp-webxml webapp)))]
        (assert (.exists web-xml))
        (assert (.isFile web-xml))
        (try (jar-cp! war-out web-xml "WEB-INF/web.xml")
             (catch ZipException e)))
      ;; all 'WEB-INF/classes/*' files
      (doseq [^File each-dir (->> [(when-not (:omit-source project) :source-paths)
                                   :resource-paths :compile-path]
                                  (remove nil?)
                                  (map project)
                                  (mapcat as-vector)
                                  (map io/file))]
        (when (.exists each-dir)
          (assert (.isDirectory each-dir))
          (println "Copying classes from" (.getAbsolutePath each-dir))
          (jar-cp-r! war-out each-dir (str "WEB-INF/classes"))))
      ;; all 'WEB-INF/lib/*' files
      (when deps?
        (doseq [^File each (->> (classpath-entries project)
                                (map io/file))]
          (when (and (.exists each) (.isFile each))
            (println "Adding library" (.getAbsolutePath each))
            (jar-cp! war-out each (str "WEB-INF/lib/" (.getName each)))))))
    (println "WAR file" warpath "is created")))
