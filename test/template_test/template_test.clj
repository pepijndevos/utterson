(load-file "../../src/utterson/core.clj")
(in-ns 'utterson.core)
(let [pages (reader "../md_files")]
  (await pages)
  (println "Rendering...")
  (println (template (last @pages) pages))
  (println "Rendering...")
  (println (template (first @pages) pages)))
(shutdown-agents)
