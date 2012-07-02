(ns leiningen.servlet.war
  "Supporting code for `war` sub command"
  (:require [clojure.java.io :as io]
            [clojure.pprint  :as pp]
            [leiningen.compile        :as lc]
            [leiningen.core.classpath :as classpath]
            [leiningen.core.main      :as main])
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


(defn generate-war
  "Generate WAR file. Generate a Uberwar (WAR with dependency libraries) when
  `deps?` is true."
  [deps? project webapp]
  (lc/compile project)
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
      ;; all static files
      (doto war-out
        (jar-cp-r! (File. (:public webapp)) "") ;; add public files
        ;; TODO Move web.xml out of public folder in templates, uncomment below
        ;; (jar-cp! (File. (:web-xml webapp)) "WEB-INF/web.xml")
        )
      ;; all 'WEB-INF/classes/*' entries
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
