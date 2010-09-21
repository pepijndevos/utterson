(ns site
  (:use utterson.compile net.cgrand.enlive-html))

(defgen page
  [[:title] (content (:title headers))
   [:#content] (html-content body)]

  [[:#menu] (append (:title headers))])

(defgen post
  [[:title] (content (:title headers))
   [:#content] (html-content body)]

  [[:#menu] (append (:title headers))
   [:body.index :#content] (prepend body)])

