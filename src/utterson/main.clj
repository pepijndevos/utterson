(ns utterson.main
  (:gen-class)
  (:use clojure.contrib.command-line)
  (:require [clojure.string :as s]))

(defn -main [& args]
  (with-command-line args
    "Execute Utterson tasks"
    [[template "Specify the template to base the current action upon"]
      [watch? "Automatically compile the give files on change"]
      [serve "Start a dev server on the given port"]
      [interactive? "Run Utterson in interactive mode"]
     task]
    (let [path (-> (clojure.lang.DynamicClassLoader.)
            (.getResource "site.clj")
            .getFile)]
      (load-file path)
      (require 'site)
      (-> task
        first
        (->> (symbol "site"))
        resolve
        deref
        (apply template task)))))
