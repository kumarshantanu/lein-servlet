(ns leiningen.servlet.war
  "Supporting code for `war` sub command"
  (:require [leiningen.core.classpath :as classpath]
            [leiningen.core.main      :as main])
  (:import (org.sonatype.aether.resolution DependencyResolutionException)))


(defn generate-war
  [project webapp-config]
  (try (let [entries (classpath/get-classpath project)]
         (println "TODO: Generate WAR file with lib (classpath)" entries))
       (catch DependencyResolutionException e
         (main/abort (.getMessage e)))))