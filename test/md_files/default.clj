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
                    (force (:body a))
                    (utterson.util/menu b)]])))
