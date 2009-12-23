(ns utterson.generator
  (:gen-class)
  (:use utterson.core))

(defn -main [& args]
  (let [pages (reader (first args))]
    (await pages)
    (doseq [p @pages]
      (writer (template p pages) (first args) (second args))))
  (shutdown-agents))
