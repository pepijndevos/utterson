(ns core
  (:import (java.io FileReader BufferedReader))
  (:import (com.petebevin.markdown MarkdownProcessor)))

(defn markdown [txt] ;Might be replaced with Showdown
  (.markdown (new com.petebevin.markdown.MarkdownProcessor) txt))

(defn parser [file]
  (loop [lines (line-seq (BufferedReader. (FileReader. file)))
         meta-data {:url (.getPath file)}]
    (let [data (re-find #"^(\w+): (.+)$" (first lines))]
      (if data
        (recur (rest lines) (assoc meta-data (keyword (second data)) (nth data 2)))
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

(defn template [page other]
  (let [path (map #(apply str (interleave % (repeat (java.io.File/separator))))
                  (iterate butlast (seq (.split (:url (last page))
                                                (java.io.File/separator))))) ; abc/def/ghi -> ("abc/def/ghi/" "abc/def/" "abc/")
        filename (.replaceAll (first path) "\\.markdown/$" ".clj")]
    (if (.exists (java.io.File. filename))
      ((load-file filename) page other)
      ((load-file (some (fn [p]
              (some #(when (= (.getName %) "default.clj") (.getPath %))
                    (.listFiles (java.io.File. p)))) path)) page other))))
