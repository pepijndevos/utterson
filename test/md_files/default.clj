(ns default
  (:require clojure.contrib.prxml))

(defn page [a b]
  (with-out-str (clojure.contrib.prxml/prxml 
                  [:html
                   [:head
                    [:title
                     (:title (second a))]]
                   [:body
                    [:h1 (:title (second a))]
                    [:raw! @(first a)]
                    (vec (cons :ul (map #(vector :li (:title (second %))) b)))]])))
