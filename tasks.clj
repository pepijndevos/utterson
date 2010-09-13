(ns user
  (:use cake cake.core)
  (:use utterson.plugin))

(deftask site
  (bake
    (:use utterson.plugin utterson.main) [] 
    (execute (first (:site *opts*)))))
