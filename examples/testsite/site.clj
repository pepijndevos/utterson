(ns site
  (:require [clojure.string :as str])
  (:use seqex
        [utterson
         markdown
         mustache]))

(def settings {:site-name "Wishfull Coding"
               :tagline "blabla"})

(defn routes [path]
  (cond-let
    [[[_ file]] (match ["pages" #"(.*)\.md" end] path)]
      [(str file "/index.html")
       :any]
    [[[_ yy mm dd file]] (match
                           ["blog"
                            #"([0-9]{2})-([0-9]{2})-([0-9]{2})-(.*)\.md"
                            end]
                           path)]
      (let [tags (str/split
                   (:tags (variables (str/join \/ (concat ["examples" "testsite"] path))))
                   #"[, ]+")]
        (concat
          (for [t tags]
            (str "tag/" t "/index.html"))
          [(str/join \/ [yy mm dd file "index.html"])
           (str file "/index.html")]))))

(process-all "." ["md" "mustache"] routes)
