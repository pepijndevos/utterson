(load-file "../../src/utterson/core.clj")
(in-ns 'utterson.core)
(let [pages (reader "../md_files")]
  (await pages)
  (println (writer (template (last @pages) pages) "../md_files" "."))
  (println (writer (template (first @pages) pages) "../md_files" ".")))
(shutdown-agents)
