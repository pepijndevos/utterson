(ns utterson.core
  (:import (java.io File FileReader BufferedReader))
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
  (loop [files (file-seq (File. dir)) pages (agent [])]
    (when (and (.isFile (first files))
               (not(.isHidden (first files)))
               (.endsWith (.getPath (first files)) ".markdown"))
      (send-off pages conj (parser (first files))))
    (if (next files)
      (recur (next files) pages)
      pages)))

(defn template [page other]
  (let [path (->> (.split (:url (last page)) (java.io.File/separator))
                  seq
                  (iterate butlast)
                  (map #(apply str (interleave % (repeat (java.io.File/separator)))))) 
        filename (.replaceAll (first path) "\\.markdown/$" ".clj")]
    (if (.exists (File. filename))
      [((load-file filename) page other) (last page)]
      [((load-file (some (fn [p]
              (some #(when (= (.getName %) "default.clj") (.getPath %))
                    (.listFiles (File. p)))) path)) page other) (last page)])))
