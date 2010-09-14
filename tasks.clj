(ns user
  (:use cake cake.core)
  (:use utterson.plugin))

(deftask site
  (bake
    (:use utterson.main utterson.plugin)
    []
    (execute (keyword (first (:site *opts*))) *opts*)))
