(ns utterson.core
  (:import (java.io File FileReader FileWriter BufferedReader BufferedWriter))
  (:import (com.petebevin.markdown MarkdownProcessor))
  (:use utterson.plugin))

(defn markdown [txt] ;Might be replaced with Showdown
  (.markdown (new com.petebevin.markdown.MarkdownProcessor) txt))

(defn src->dest [meta-data #^String dir #^String dest]
  (assoc meta-data :dest
         (-> #^String (:src meta-data)
           (.replaceAll (.getCanonicalPath (File. dir)) (.getCanonicalPath (File. dest)))
           (.replaceAll "\\.markdown$" #^String (:extension meta-data ".html")))))

(defn src->url [meta-data #^String dir]
  (assoc meta-data :url
         (-> #^String (:src meta-data)
           (.replaceAll (.getCanonicalPath (File. dir)) "") ;needs a relative path!
           (.replaceAll "\\.markdown$" #^String (:extension meta-data ".html")))))

(defn parser [#^File file #^String dir #^String dest]
  (loop [lines (line-seq (BufferedReader. (FileReader. file)))
         meta-data {:src (.getCanonicalPath file)}]
    (let [data (re-find #"^(\w+): (.+)$" (first lines))]
      (if data
        (recur (rest lines) (assoc meta-data (keyword (second data)) (nth data 2)))
        (do-action :filter [(future (markdown (apply str (interpose \newline lines))))
                            (src->url (src->dest meta-data dir dest) dir)])))))

(defn reader [#^String dir #^String dest]
  (loop [files (file-seq (File. dir)) pages (agent (do-action :start []))]
    (when (and (.isFile #^File (first files))
               (not(.isHidden #^File (first files)))
               (.endsWith (.getCanonicalPath #^File (first files)) ".markdown"))
      (send-off pages conj (parser (first files) dir dest)))
    (if (next files)
      (recur (next files) pages)
      (do-action :all-content pages))))

(defn template [page other]
  (let [path (->> (.split #^String (:src (last page)) (File/separator))
                  seq
                  (iterate butlast)
                  (map #(apply str (interleave % (repeat (java.io.File/separator)))))) 
        filename (.replaceAll #^String (first path) "\\.markdown/$" ".clj")]
    (if (.exists (File. filename))
      [((load-file filename) page other) (last page)]
      [((load-file (some (fn [#^String p]
              (some #(when (= (.getName #^File %) "default.clj") (.getCanonicalPath #^File %))
                    (.listFiles (File. p)))) path)) page other) (last page)])))

(defn writer [pages]
  (doseq [page (do-action :all-template pages)]
    (.mkdirs (.getParentFile (File. #^String (:dest (last page)))))
    (with-open [file (BufferedWriter.
                 (FileWriter. #^String (:dest (last page))))]
      (.write file #^String (first page)))))
