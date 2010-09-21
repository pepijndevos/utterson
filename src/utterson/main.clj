(ns utterson.main
  (:gen-class)
  (:use clojure.contrib.command-line)
  (:require [clojure.string :as s]))

(defn -main [& args]
  (with-command-line args
    "Execute Utterson tasks"
    [[template "Specify the template to base the current action upon"]
    task]
    (let [path (-> (clojure.lang.DynamicClassLoader.)
            (.getResource "site.clj")
            .getFile)]
      (load-file path)
      (require 'site)
      (-> task
        drop-last
        (->> (s/join \-)
             (symbol "site"))
        resolve
        deref
        (apply [(last task) template])))))

;(apply -main *command-line-args*)
