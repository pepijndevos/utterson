(ns utterson.markdown
  (:use utterson.plugin)
  (:require [clj-yaml.core :only parse-string])
  (:require [clojure.contrib.duck-streams :only read-lines]))

(defn parse
  "Parse file at path returning a vector [headers content]
  where headers is parsed with YAML up to \n\n
  and content the rest of the file using Markdown"
  [path]
  (let [[headers content] (split-with
                            #(not= "" %)
                            (clojure.contrib.duck-streams/read-lines path))
        headers (apply str (interleave headers (repeat \newline)))
        content (apply str (interleave content (repeat \newline)))]
    [(execute :headers  (clj-yaml.core/parse-string headers))
     (execute :markdown (.(org.pegdown.PegDownProcessor.)
                          markdownToHtml content))]))
