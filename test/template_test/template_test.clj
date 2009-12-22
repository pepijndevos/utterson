(load-file "../../src/utterson/core.clj")
(in-ns 'core)
(let [pages (reader "../md_files")]
  (await pages)
  (println "Rendering" (:url (last (last @pages))))
  (println (template (last @pages) @pages))
  (println "Rendering" (:url (last (first @pages))))
  (println (template (first @pages) @pages)))
