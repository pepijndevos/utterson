(ns utterson.main
  (:gen-class)
  (:use clojure.contrib.command-line)
  (:require [clojure.string :as s])
  (:require site))

(defn -main [& args]
  (with-command-line args
    "Execute Utterson tasks"
    [[template "Specify the template to base the current action upon"]
    task]
    (-> task
      drop-last
      (->> (s/join \-)
           (symbol "site"))
      resolve
      deref
      (apply [(last task) template]))))

(apply -main *command-line-args*)
