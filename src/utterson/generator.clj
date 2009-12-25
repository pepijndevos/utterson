(ns utterson.generator
  (:gen-class)
  (:use utterson.core))

;(set! *warn-on-reflection* true)

(defn -main [& args]
  (let [pages (reader (first args) (second args))]
    (await pages)
    (writer (map #(template % @pages) @pages)))
  (shutdown-agents))

;(apply -main *command-line-args*)
