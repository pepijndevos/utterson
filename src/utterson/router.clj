(ns utterson.router
  (:use utterson.plugin
        utterson.markdown)
  (:require clojure.java.io
            ring.util.response))

(defn utter
  "Ring middleware for calling your template
  with the parsed Markdown file."
  [template]
  (fn [req]
    (let [file (:uri req)
          abspath (.getFile
                    (.getResource
                      (class utter)
                      "/source"))
          file (if (#'ring.util.response/find-index-file
                     (java.io.File. (str
                                      abspath
                                      file)))
                 (str "/source" file "/index.md")
                 (str "/source" file ".md"))
          [headers content] (parse file)]
      {:status 200
       :headers {"Content-Type" "text/html;charset=UTF-8"}
       :body (apply str (template content headers))})))

