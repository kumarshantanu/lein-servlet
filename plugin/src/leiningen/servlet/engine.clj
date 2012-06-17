(ns leiningen.servlet.engine
  "Access servlet engines"
  (:use leiningen.servlet.util)
  (:require [clojure.string :as str]
            [bultitude.core :as bult]))


(def engine-ns-prefix "leiningen.servlet.engine")


(defn engine-namespaces
  "Return a seq of [eng-ns eng-name] symbol pairs of corresponding available
  servlet engines."
  []
  (let [ens (->> (bult/namespaces-on-classpath :prefix engine-ns-prefix)
                 (filter #(re-find #"^leiningen\.servlet\.engine\.[^\.]+$"
                                   (name %)))
                 distinct
                 sort)]
    (map #(vector % (-> % name (str/split #"\.") last symbol)) ens)))


(defn engine-entrypoint
  "Resolve engine entrypoint and return the value, else return nil."
  [eng-ns-symbol eng-name-symbol] {:pre  [(symbol? eng-ns-symbol)
                                          (symbol? eng-name-symbol)]
                                   :post [(or (nil? %) (fn? %))]}
  (echo "Going to resolve engine entry-point" [eng-ns-symbol eng-name-symbol])
  (when-let [v  (-> eng-ns-symbol
                    (doto require)
                    (ns-resolve eng-name-symbol))]
    @v))


(defn choose-engine
  "Return a [eng-ns eng-name] symbol pair based on config"
  [wanted-eng eng-namespaces] {:pre [(coll? eng-namespaces)
                                     (every? vector? eng-namespaces)
                                     (every? #(= 2 (count %)) eng-namespaces)
                                     (every? #(every? symbol? %) eng-namespaces)]}
  (let [wanted-eng (and wanted-eng (as-str wanted-eng))
        eng-varmap (reduce merge {} (map #(do {(-> % second name) %})
                                         eng-namespaces))]
    (cond (empty? eng-varmap) (err-println "No servlet engine on CLASSPATH")
          (nil? wanted-eng)   (-> eng-varmap vals first)
          :otherwise          (if-let [e (get eng-varmap wanted-eng)]
                                e
                                (err-println
                                 (format "No such engine '%s', found: %s"
                                         wanted-eng
                                         (comma-sep eng-varmap)))))))


;; (defn apply-config
;;   [f project args]
;;   (let [config (:servlet project)]
;;     (if (map? config)
;;       (f config args)
;;       (err-println "No :servlet map found in project.clj - see help"))))
