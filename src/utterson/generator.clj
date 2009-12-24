(ns utterson.generator
  (:gen-class)
  (:use utterson.core))

(defn -main [& args]
  (let [pages (reader (first args))]
    (await pages)
    (writer (map #(template % @pages (first args) (second args)) @pages)))
  (shutdown-agents))

;(apply -main *command-line-args*)
