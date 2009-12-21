(ns core
  (:import (java.io FileReader BufferedReader))
  (:import (com.petebevin.markdown MarkdownProcessor)))

(def pages (agent [])) ;Agent?

(defn markdown [txt] ;Might be replaced with Showdown
  (.markdown (new com.petebevin.markdown.MarkdownProcessor) txt))

(defn parser [file]
  (loop [lines (line-seq (BufferedReader. (FileReader. file))) meta-data {}]
    (if (.contains (first lines) ":")
      (let [data (re-find #"^(\w+): (.+)$" (first lines))]
        (recur (rest lines) (assoc meta-data (second data) (nth data 2))))
      [(future (markdown (apply str (interpose \newline (rest lines))))) meta-data])))

(defn reader [dir]
  (loop [files (file-seq (java.io.File. dir))]
    (when (and (.isFile (first files))
               (not(.isHidden (first files)))
               (.endsWith (.getPath (first files)) ".markdown")) ;Other extension?
      (prn (first files))
      (send-off pages conj (parser (first files))))
    (when (next files) (recur (next files)))))
