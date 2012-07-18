(ns leiningen.servlet
  "Work with servlets in Java or other JVM languages"
  (:use leiningen.servlet.util)
  (:require [clojure.java.browse :as browse]
            [leiningen.servlet.engine :as eng]
            [leiningen.servlet.war    :as war]
            [leiningen.core.eval      :as eval]
            [cemerick.pomegranate     :as pome])
  (:import (java.io File FileNotFoundException)))


(defn update-project-deps
  [project deps]
  (echo "Updating project-map with :dependencies" deps)
  (if deps
    (let [new-proj (apply update-in project [:dependencies] conj deps)
          new-deps (:dependencies new-proj)]
      (echo "Returning new-deps for project" new-deps)
      new-proj)
    (do (echo "No change to project :dependencies" (:dependencies project))
        project)))


(defn list-engines
  "Print a list of available engines and the one chosen to be run.
  (Execute this after loading deps.)"
  [wanted-eng]
  (let [ens (eng/engine-namespaces)]
    (println "Found engines:" (count ens))
    (doseq [[the-ns the-name] ens]
      (println the-ns (str "(" the-name ")")))
    (println "\nEngine to be used:" (eng/choose-engine wanted-eng ens))))


(defn load-deps
  [deps repos] {:pre [(vector? deps) (every? vector? deps)
                      (or (nil? repos) (map? repos))]}
  (echo "Loading dependencies in lein-servlet" deps)
  (pome/add-dependencies
   :coordinates deps
   :repositories (merge cemerick.pomegranate.aether/maven-central
                        {"clojars" "http://clojars.org/repo"}
                        repos)))


;; ------------------ Project specific ------------------


(def CONFIG     [:servlet :config])
(def DEPS       [:servlet :deps])
(def HOST       [:servlet :config :host])
(def PORT       [:servlet :config :port])
(def REPOS      [:servlet :repos])
(def WANTED-ENG [:servlet :config :engine])
(def WEBAPPS    [:servlet :webapps])
(def SSL        [:servlet :config :ssl])
(def SSL-PORT   [:servlet :config :ssl :port])
(def SSL-KSPATH [:servlet :config :ssl :keystore-path])
(def WAR-EX     [:servlet :war-exclusions])
(def UBERWAR-EX [:servlet :uberwar-exclusions])


(defn with-defaults
  "Populate :servlet config map with static default values"
  [config] {:pre [(map? config)
                  (map? (:servlet config))]}
  (let [def-ks-path   (format "%s/.keystore" (System/getProperty "user.dir"))
        ks-path-check (fn [config]
                       (let [ks-path (get-in config SSL-KSPATH)]
                         (if (.exists (File. ks-path)) config
                             (throw (FileNotFoundException.
                                     (format "Keystore file '%s' not found"
                                             ks-path))))))
        ssl-defaults  (fn [config] ;; SSL defaults
                        (if (get-in config SSL)
                          (-> config
                              (update-in SSL-PORT (fnil identity 3443))
                              (update-in SSL-KSPATH (fnil identity def-ks-path))
                              ks-path-check)
                          config))]
    (-> config
        (update-in HOST (fnil identity "localhost"))
        (update-in PORT (fnil identity 3000))
        ssl-defaults)))


(defn engine
  [project]
  (let [deps  (get-in project DEPS)
        repos (get-in project REPOS)]
    (when deps (load-deps deps repos)))
  (list-engines (get-in project WANTED-ENG)))


(defn run
  "Run servlet or webapp as per configuration"
  [project args]
  (let [deps    (get-in project DEPS)
        repos   (get-in project REPOS)]
    (when deps (load-deps deps repos)))
  (let [project    (-> project
                       with-defaults
                       (update-project-deps (get-in project DEPS)))
        wanted-eng (get-in project WANTED-ENG)
        config     (get-in project CONFIG)
        webapps    (get-in project WEBAPPS)
        [ens enam] (eng/choose-engine wanted-eng (eng/engine-namespaces))
        full-name  (symbol (str ens "/" enam))
        join-name  (symbol (str ens "/join"))
        port       (get-in project PORT)
        config     (merge {:tmpdir (str (:target-path project) \/ enam \. port)}
                          config)
        home-url   (str "http://localhost:" port)]
    (if (and ens enam)
      (eval/eval-in-project project `(let [e# (~full-name ~config ~webapps)]
                                       (clojure.java.browse/browse-url ~home-url)
                                       (~join-name e#))
                            `(do (require '~ens)
                                 (require 'clojure.java.browse)))
      (err-println "Unable to determine engine"))))


(defn show-help
  "Show help for this plugin"
  []
  (println "
Synopsis:
    lein servlet <command> [<arguments> ..]

where <command> and respective <arguments> are:
    run
    war  [<app-name>]
    help

To generate a new project template containing example configuration:
    lein new servlet demo
")
  (flush))


(defn war
  "Generate WAR file from auto-detected (default) or specified webapp name"
  [war-maker project [app-name]]
  (let [wa-conf (get-in project WEBAPPS)
        webapps (zipmap (map as-str (keys wa-conf))
                        (vals wa-conf))
        w-names (set (keys webapps))
        v-names (format "(valid names: %s)" (comma-sep (keys webapps)))]
    (cond (empty? w-names)      (do (err-println "No webapps configured")
                                    (show-help))
          (w-names app-name)    (war-maker project (get webapps app-name))
          app-name              (err-println "No such webapp name" app-name v-names)
          (= (count w-names) 1) (war-maker project (get webapps (first w-names)))
          :otherwise            (err-println "Must specify app-name" v-names))))


(defn servlet
  "Work with servlets in Java or another JVM language."
  [project & [cmd & args]]
  (case cmd
    nil       (do (show-help) 1)
    "engine"  (engine project)
    "run"     (run project args)
    "uberwar" (war (partial war/generate-war true (get-in project WAR-EX))
                   project args)
    "war"     (war (partial war/generate-war false (get-in project UBERWAR-EX))
                   project args)
    "help"    (show-help)
    (do (err-println "Invalid subcommand" cmd "\n")
        (show-help)
        1)))
