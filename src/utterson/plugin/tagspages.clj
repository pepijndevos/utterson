(ns utterson.plugin.testplugin
  (:require clojure.contrib.prxml)
  (:use utterson.plugin)
  (:use utterson.util)
  (:use utterson.core))

(defn parse-tags [string]
  (set (.split (str string) ", ")))

(defn tag-template [tag pages]
  (with-out-str (clojure.contrib.prxml/prxml
                  [:html
                   [:head
                    [:title tag]]
                   [:body
                    [:h1 tag]
                    [:raw! (utterson.util/menu (filter #(get (parse-tags (:tags (second %))) tag) pages))]]])))

(defn tags [pages]
  (let [p (filter #(:tags (second %)) pages) tags (reduce #(into %1 (parse-tags (:tags (second %2)))) #{} p)]
    (reduce #(conj %1 (do-action :filter [(tag-template %2 pages)
                       {:url (str "/tags/" %2 ".html")
                        :dest (.getCanonicalPath (java.io.File. (str (last *command-line-args*) "/tags/" %2 ".html")))
                        :title %2}])) pages tags)))

(register :all tags)
