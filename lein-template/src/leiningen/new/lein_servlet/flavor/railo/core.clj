(ns {{name}}.core)


(defn sum [n & more]
  "Return sum of numbers"
  (apply + n more))


(defn -main [& args]
  (println "
Please invoke this app as follows:

$ lein2 servlet run"))
