(ns utterson.markdown
  (:use utterson.plugin)
  (:use [clj-yaml.core :only [parse-string]])
  (:use [clojure.contrib.duck-streams :only [read-lines]]))

(defn parse
  "Parse file at path returning a vector [headers content]
  where headers is parsed with YAML up to \n\n
  and content the rest of the file using Markdown"
  [path]
  (let [[headers content] (split-with
                            #(not= "" %)
                            (read-lines path))
        headers (apply str (interleave headers (repeat \newline)))
        content (apply str (interleave content (repeat \newline)))]
    (cons (execute :headers  (parse-string headers))
          (lazy-seq (list (execute :markdown (.(org.pegdown.PegDownProcessor.)
                                    markdownToHtml content)))))))
