(ns utterson.plugin.testplugin
  (:use utterson.plugin))

(register :filter #(do (println (:url %)) %))
;(register :all-content #(doall %))
