(ns utterson.main
  (:use utterson.plugin)
  (:use site)
  (:require ring.adapter.jetty
            ring.middleware.file
            ring.middleware.file-info
            clojure.java.io))

;define tasks here
;tasks executed with Cake opts
(register :echo #(println %))

(register :preview
  (fn preview [_]
    (let [path (.getFile
                 (.getResource (class site) "source"))
          handler (org.mortbay.jetty.handler.ResourceHandler.)]
      ;(.setDirectoriesListed handler true)
      (.setResourceBase handler path)
      (ring.adapter.jetty/run-jetty
        site
        {:port 8080
         :configurator #(.addHandler % handler)}))))
