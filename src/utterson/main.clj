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
    (let [site (-> site
                 (ring.middleware.file/wrap-file
                   (.getFile
                     (.getResource (class site) "source")))
                 ring.middleware.file-info/wrap-file-info)]
      (ring.adapter.jetty/run-jetty site {:port 8080}))))
