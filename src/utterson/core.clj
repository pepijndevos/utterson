(ns utterson.core
  (:import (java.io File FileReader FileWriter BufferedReader BufferedWriter))
  (:import (com.petebevin.markdown MarkdownProcessor))
  (:use utterson.util)
  (:use utterson.plugin))

(defn markdown [txt] ;Might be replaced with Showdown
  (.markdown (new com.petebevin.markdown.MarkdownProcessor) txt))

(defn src->dest [page]
  (assoc page :dest
         (-> #^String (:src page)
           (.replaceAll (.getCanonicalPath (File. #^String (:srcdir page)))
                        (.getCanonicalPath (File. #^String (:destdir page))))
           (.replaceAll "\\.markdown$" #^String (:extension page ".html")))))

(defn src->url [page]
  (assoc page :url
         (-> #^String (:src page)
           (.replaceAll (.getCanonicalPath (File. #^String (:srcdir page))) "") ;needs a relative path!
           (.replaceAll "\\.markdown$" #^String (:extension page ".html"))
           (.replaceAll "[^a-zA-Z/\\.]" ""))))

(defn parser [page]
  (let [file (:body page)
        [meta body] (split-with #(re-find #"^(\w+): (.+)$" %)
                                (line-seq (BufferedReader. (FileReader. file))))]
    (do-action :filter
               (src->dest
                 (src->url
                   (into
                     (->> (interpose \newline body)
                          (apply str)
                          markdown
                          (assoc page :src (.getCanonicalPath file) :body))
                     (map #(let [data (re-find #"^(\w+): (.+)$" %)]
                             [(keyword (.toLowerCase (second data))) (nth data 2)]) meta)))))))

(defn reader [#^String src #^string dest]
  (do-action :all-content
             (for [pages (do-action :files (file-seq (File. src)))
                   :when (and (.isFile #^File pages)
                              (not (.isHidden #^File pages))
                              (.endsWith (.getCanonicalPath #^File pages) ".markdown"))]
               (parser (struct page-struct pages src dest)))))

(defn template
  ([other]
   (do-action :all-template (map #(template % other) other)))
  ([page other]
   (let [path (iterate #(.getParentFile %) (File. (:src page)))
         filename (.replaceAll #^String (:src page) "\\.markdown$" ".clj")]
     (if (.exists (File. filename))
       ((load-file filename) page other)
       (maze-thread (fn [#^File   f] (when (= (.getName f) "default.clj") (.getCanonicalPath f)))
                    (fn [#^String p] (some % (.listFiles p)))
                    ((load-file (some % path)) page other))))))

(defn writer [pages]
  (doseq [page pages]
    (.mkdirs (.getParentFile (File. #^String (:dest page))))
    (with-open [file (BufferedWriter.
                       (FileWriter. #^String (:dest page)))]
      (.write file #^String (:body page)))))
