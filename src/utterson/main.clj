(ns utterson.main
  (:use utterson.plugin)
  (:require ring.adapter.jetty
            ring.middleware.file
            ring.middleware.file-info
            clojure.java.io))

;define tasks here
;tasks executed with Cake opts
(register :echo #(println %))

(register :preview
  (fn preview [path]
    (let [site (load-file
                 (.getAbsolutePath
                   (clojure.java.io/file path "site.clj")))
          site (-> site
                 (ring.middleware.file/wrap-file
                   (.getAbsolutePath
                     (clojure.java.io/file path "source")))
                 ring.middleware.file-info/wrap-file-info)]
      (ring.adapter.jetty/run-jetty site {:port 8080}))))
