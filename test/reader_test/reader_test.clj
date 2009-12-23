(load-file "../../src/utterson/core.clj")
(in-ns 'utterson.core)
(println "Reading...")
(let [pages (time (reader "../md_files"))]
  (println "Waiting...")
  time (await pages))
  (println "Printing last result:")
  (time (println (last @pages))))
(shutdown-agents)
