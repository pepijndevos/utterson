(ns utterson.plugin.testplugin
  (:use utterson.plugin)
  (:use utterson.util))

(defn parse-tags [string]
  (set (.split (str string) ", ")))

(defn tags [pages]
  (let [p (filter #(:tags %) pages) tags (reduce #(into %1 (parse-tags (:tags %2))) #{} p)]
    (reduce #(conj %1 (do-action :filter 
                                 (struct page-struct
                                         %2 ;body contains the tag
                                         (:srcdir (first %1))
                                         (:destdir (first %1))
                                         (.getCanonicalPath
                                           (java.io.File.
                                             (str (:srcdir (first %1)) "/tags/" %2 ".markdown")))
                                         (.getCanonicalPath
                                           (java.io.File.
                                             (str (:destdir (first %1)) "/tags/" %2 ".html")))
                                         (str "/tags/" %2 ".html")
                                         %2))) pages tags)))

(register :all-content tags)
