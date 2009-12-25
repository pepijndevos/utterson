(ns utterson.plugin.testplugin
  (:use utterson.plugin))

;(register :filter #(do (println (hash %)) %))
(println (loaded-libs))
