(ns utterson.core
  (:import (java.io File FileReader FileWriter BufferedReader BufferedWriter))
  (:import (com.petebevin.markdown MarkdownProcessor))
  (:use utterson.plugin)
  (:use utterson.util))

(defn markdown [txt] ;Might be replaced with Showdown
  (.markdown (new com.petebevin.markdown.MarkdownProcessor) txt))

(defn src->dest [page #^String src #^String dest]
  (assoc page :dest
         (-> #^String (:src page)
           (.replaceAll (.getCanonicalPath (File. src)) (.getCanonicalPath (File. dest)))
           (.replaceAll "\\.markdown$" #^String (:extension (meta page) ".html")))))

(defn src->url [page #^String src]
  (assoc page :url
         (-> #^String (:src page)
           (.replaceAll (.getCanonicalPath (File. src)) "") ;needs a relative path!
           (.replaceAll "\\.markdown$" #^String (:extension (meta page) ".html"))
           (.replaceAll "[^a-zA-Z/\\.]" ""))))

(defn parser [#^File file #^String src #^String dest]
  (loop [lines (with-meta (line-seq (BufferedReader. (FileReader. file)))
                          {:src (.getCanonicalPath file)})]
    (let [data (re-find #"^(\w+): (.+)$" (first lines))]
      (if data
        (recur (with-meta (rest lines) (assoc (meta lines) (keyword (second data)) (nth data 2))))
        (maze-thread (apply str (interpose \newline lines))
                     (markdown %)
                     (assoc (meta lines) :body %)
                     (src->dest % src dest)
                     (src->url % src)
                     (do-action :filter %))))))

(defn reader [#^String src #^string dest]
  (do-action :all-content (with-meta (for [pages (do-action :files (file-seq (File. src)))
                                           :when (and (.isFile #^File pages)
                                                      (not (.isHidden #^File pages))
                                                      (.endsWith (.getCanonicalPath #^File pages) ".markdown"))]
                                       (parser pages src dest)) {:src src, :dest dest})))

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
