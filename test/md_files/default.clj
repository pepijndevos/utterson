(ns default
  (:require clojure.contrib.prxml)
  (:require utterson.util))

(defn page [a b]
  (assoc a :body (with-out-str (clojure.contrib.prxml/prxml 
                  [:html
                   [:head
                    [:title
                     (:title a)]]
                   [:body
                    [:h1 (:title a)]
                    [:raw! (:body a)]
                    [:raw! (utterson.util/menu b)]]]))))
