(ns utterson.plugin)

(def hooks (atom {}))

(defn register
  "Register function to be run with the input given when hook is executed,
  or with the result of previuous functions attached to this hook."
  [hook function]
  (swap! hooks update-in [hook] conj function))

(defn execute
  "Execute the comp of all functions attached to hook"
  ([hook] ((apply comp (hook @hooks))))
  ([hook arg] ((apply comp (hook @hooks)) arg))
  ([hook arg & args] (apply (apply comp (hook @hooks)) arg args)))
