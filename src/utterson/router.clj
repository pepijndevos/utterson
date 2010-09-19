(ns utterson.router
  (:use utterson.plugin
        utterson.markdown)
  (:require clojure.java.io
            ring.util.response))

(def pages (atom #{}))

(register :headers
  (fn [yaml]
    (swap! pages conj yaml)
    yaml))

(defn utter
  "Ring middleware for calling your template
  with the parsed Markdown file."
  [template]
  (fn [req]
    (let [file (str source-path (:uri req))
          file (if (#'ring.util.response/find-index-file
                     (java.io.File. file))
                 (if (= "/" (:uri req))
                   (str file "index.md")
                   (str file "/index.md"))
                 (str file ".md"))
          [headers content] (parse file)]
      {:status 200
       :headers {"Content-Type" "text/html;charset=UTF-8"}
       :body (apply str (template content headers @pages))})))

