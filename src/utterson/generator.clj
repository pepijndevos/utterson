(ns utterson.generator
  (:gen-class)
  (:use utterson.core))

(set! *warn-on-reflection* true)

(defn -main [& args]
  (let [pages (reader (first args) (second args))]
    (await pages)
    (->> @pages
         (map #(template % @pages))
         writer))
  (shutdown-agents))

(apply -main *command-line-args*)
