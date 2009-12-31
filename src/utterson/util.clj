(ns utterson.util
  (:require compojure.html))

(defn menu "Generate a simple list of a list of pages."
  [pages]
  (compojure.html/html
    (vec (cons :ul
          (map #(vector :li [:a {:href (:url %), :title (:title %)} (:title %)]) (filter #(:title %) pages))))))

(defmacro maze-thread "Threads the expr through the forms at the position of %."
  [& expr] `(let [~'% ~@(interpose '% expr)] ~'%))
