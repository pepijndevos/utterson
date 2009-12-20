(ns core
  (:import (java.io FileReader BufferedReader))
  (:import (com.petebevin.markdown MarkdownProcessor)))

(def pages (ref []))

(defn markdown [txt]
  (.markdown (new com.petebevin.markdown.MarkdownProcessor) txt))

(defn parser [file-handle]
  (loop [lines (line-seq file-handle) meta-data {}]
    (if (= "" (first lines))
      [(future (markdown (apply str (interpose \newline (rest lines))))) meta-data]
      (let [data (re-find #"(\w+): (\w+)" (first lines))]
        (recur (rest lines) (assoc meta-data (second data) (nth data 2)))))))
