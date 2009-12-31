(ns utterson.plugin.testplugin
  (:use utterson.plugin))

(register :filter #(do (prn (:url %) (:tags %) (:title %)) %))
