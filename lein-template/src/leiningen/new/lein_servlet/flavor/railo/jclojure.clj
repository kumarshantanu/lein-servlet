(ns {{name}}.util.jclojure
  (:gen-class
   :name {{sanitized}}.util.jclojure
   :methods [#^{:static true} [getvar [String String] clojure.lang.Var]]))


(defn as-str
  [x]
  (if (or (keyword? x) (symbol? x)) (name x)
      (str x)))


(defn as-symbol
  [x]
  (symbol (as-str x)))


(defn -getvar
  "Resolve a var in given namespace"
  [the-ns the-name]
  (let [s-ns   (as-symbol the-ns)
        s-name (as-symbol the-name)]
    (-> s-ns
        as-symbol
        (doto require)
        (ns-resolve s-name))))
