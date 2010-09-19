(ns utterson.main
  (:use utterson.plugin
        site)
  (:require ring.adapter.jetty
            ring.middleware.file
            ring.middleware.file-info
            [clojure.contrib.seq-utils :as su]))

;define tasks here
;tasks executed with Cake opts
(register :echo #(println %))

(register :preview
  (fn preview [_]
    (let [handler (org.mortbay.jetty.handler.ResourceHandler.)]
      (.setResourceBase handler source-path)
      (ring.adapter.jetty/run-jetty
        site
        {:port 8080
         :configurator #(.addHandler % handler)}))))

(register :compile
  (fn compile-site [_]
    (su/separate
      #(.endsWith (.getPath %) ".md")
      (file-seq source-dir))))
(load "compile")
