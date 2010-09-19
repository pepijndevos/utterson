(ns utterson.plugin)

(def source-path (.getFile (.getResource (clojure.lang.DynamicClassLoader.) "source")))
(def source-dir (java.io.File. source-path))

(def hooks (atom {}))

(defn register
  "Register function to be run with the input given when hook is executed,
  or with the result of previuous functions attached to this hook."
  [hook function]
  (swap! hooks update-in [hook] conj function))

(defn execute
  "Execute the comp of all functions attached to hook"
  [hook arg]
  ((apply comp identity (hook @hooks)) arg))
