(ns utterson.plugin.testplugin
  (:use utterson.plugin))

(register :filter #(do (prn (str (.substring @(first %) 0 50) "...")) %))
