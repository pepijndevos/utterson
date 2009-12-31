(ns default
  (:require compojure.html)
  (:require utterson.util))

(defn page [a b]
  (assoc a :body (compojure.html/html
                  [:html
                   [:head
                    [:title
                     (:title a)]]
                   [:body
                    [:h1 (:title a)]
                    (utterson.util/menu (filter #(.contains (or #^String (:tags %) "") #^String (:body a)) b))]])))
