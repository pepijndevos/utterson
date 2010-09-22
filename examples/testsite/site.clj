(ns site
  (:use utterson.compile net.cgrand.enlive-html))

(defsnippet menu-item "index.html" [:#menu [:li first-child]] [id title href]
  [:a] (content title)
  [:a] (set-attr :href (str \/ href))
  [:li] (set-attr :id id))

(defgen page
  [[:title] (content (:title headers))
   [:#content] (html-content body)]

  [[:#menu] (append-or-update [(id= (:id headers))]
                              (menu-item
                                (:id headers)
                                (:title headers)
                                (:path headers)))])
