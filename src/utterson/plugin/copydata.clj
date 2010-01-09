(ns utterson.plugin.copydata
  (:use utterson.plugin)
  (:import (java.io File FileInputStream FileOutputStream)))

(defn copy [[src dest]]
  (doseq [pages (file-seq (File. src))
        :when (and (.isFile #^File pages)
                   (not (.isHidden #^File pages))
                   (not (.endsWith (.getPath #^File pages) ".markdown"))
                   (not (.endsWith (.getPath #^File pages) ".clj")))]
    (let [in  (.getChannel (FileInputStream.  pages))
          out (.getChannel (FileOutputStream. (File. (.replaceAll (.getPath pages) src dest))))]
      (println (.getPath pages))
      (.transferTo in 0 (.size in) out))))
    
(register :writing copy)
