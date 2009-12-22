(load-file "../../src/utterson/core.clj")
(in-ns 'core)
(println "Reading...")
(let [pages (time (reader "md_files"))]
  (println "Waiting...")
  (time (await pages))
  (println "Printing last result:")
  (time (println @(first (last @pages)))))
