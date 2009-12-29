(ns utterson.plugin)

(def actions (atom {}))

(defn register [action function]
  (swap! actions assoc action (conj (action @actions) function)))

(defn do-action [action arg]
  (println "doing" action)
  (reduce #(%2 %1) arg (action @actions)))

(doseq [file (->> (file-seq (java.io.File. "utterson/plugin"))
                  (filter #(and (.isFile %) (not (.isHidden %)) (.endsWith (.getPath %) ".clj"))))]
  (load-file (.getPath file)))
