(ns utterson.util
  (:require clojure.contrib.prxml))

(defn menu "Generate a simple list of a list of pages."
  [pages]
  (with-out-str (clojure.contrib.prxml/prxml
    (cons :ul
          (map #(vector :li [:a {:href (:url %), :title (:title %)} (:title %)]) (filter #(:title %) pages))))))

(defmacro maze-thread "Threads the expr through the forms at the position of %."
  [& expr] `(let [~'% ~@(interpose '% expr)] ~'%))

