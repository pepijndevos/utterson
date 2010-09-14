(ns user
  (:use cake cake.core)
  (:use utterson.plugin))

(deftask site
  (bake
    (:use utterson.main utterson.plugin) []
    (try
      (execute (keyword (first (:site *opts*))) *opts*)
      (catch java.lang.IllegalArgumentException e (println "Unkown command")))))
