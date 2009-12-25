(ns default
  (:require clojure.contrib.prxml)
  (:require utterson.util))

(defn page [a b]
  (with-out-str (clojure.contrib.prxml/prxml 
                  [:html
                   [:head
                    [:title
                     (:title (second a))]]
                   [:body
                    [:h1 (:title (second a))]
                    [:raw! @(first a)]
                    [:raw! (utterson.util/menu b)]]])))
