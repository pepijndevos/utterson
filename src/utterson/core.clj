(ns core
  (:import (java.io FileReader BufferedReader))
  (:import (com.petebevin.markdown MarkdownProcessor)))

(defn markdown [txt] ;Might be replaced with Showdown
  (.markdown (new com.petebevin.markdown.MarkdownProcessor) txt))

(defn parser [file]
  (loop [lines (line-seq (BufferedReader. (FileReader. file))) meta-data {}]
    (let [data (re-find #"^(\w+): (.+)$" (first lines))]
      (if data
        (recur (rest lines) (assoc meta-data (second data) (nth data 2)))
        [(future (markdown (apply str (interpose \newline lines)))) meta-data]))))

(defn reader [dir]
  (loop [files (file-seq (java.io.File. dir)) pages (agent [])]
    (when (and (.isFile (first files))
               (not(.isHidden (first files)))
               (.endsWith (.getPath (first files)) ".markdown"))
      (send-off pages conj (parser (first files))))
    (if (next files)
      (recur (next files) pages)
      pages)))
