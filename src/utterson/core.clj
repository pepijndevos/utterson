(ns utterson.core
  (:import (java.io File FileReader FileWriter BufferedReader BufferedWriter))
  (:import (com.petebevin.markdown MarkdownProcessor)))

(defn markdown [txt] ;Might be replaced with Showdown
  (.markdown (new com.petebevin.markdown.MarkdownProcessor) txt))

(defn parser [#^File file]
  (loop [lines (line-seq (BufferedReader. (FileReader. file)))
         meta-data {:url (.getPath file)}]
    (let [data (re-find #"^(\w+): (.+)$" (first lines))]
      (if data
        (recur (rest lines) (assoc meta-data (keyword (second data)) (nth data 2)))
        [(future (markdown (apply str (interpose \newline lines)))) meta-data]))))

(defn reader [#^String dir]
  (loop [files (file-seq (File. dir)) pages (agent [])]
    (when (and (.isFile #^File (first files))
               (not(.isHidden #^File (first files)))
               (.endsWith (.getPath #^File (first files)) ".markdown"))
      (send-off pages conj (parser (first files))))
    (if (next files)
      (recur (next files) pages)
      pages)))

(defn template [page other]
  (let [path (->> (.split #^String (:url (last page)) (File/separator))
                  seq
                  (iterate butlast)
                  (map #(apply str (interleave % (repeat (java.io.File/separator)))))) 
        filename (.replaceAll #^String (first path) "\\.markdown/$" ".clj")]
    (if (.exists (File. filename))
      [((load-file filename) page other) (last page)]
      [((load-file (some (fn [#^String p]
              (some #(when (= (.getName #^File %) "default.clj") (.getPath #^File %))
                    (.listFiles (File. p)))) path)) page other) (last page)])))

(defn writer [page #^String dir #^String dest]
  (-> #^String (:url (last page))
    (.replaceAll dir dest)
    (.replaceAll "\\.markdown$" #^String (:extension (last page) ".html"))
    FileWriter.
    BufferedWriter.
    (doto (.write #^String (first page)) (.close))))
