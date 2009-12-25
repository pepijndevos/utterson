(ns utterson.util
  (:require clojure.contrib.prxml))

(defn menu [pages]
  (with-out-str (clojure.contrib.prxml/prxml
    (cons :ul
          (map #(vector :li [:a {:href (:dest (last %)), :title (:title (last %))} (:title (last %))]) (filter #(:title (second %)) pages))))))
