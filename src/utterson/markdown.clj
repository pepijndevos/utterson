(ns utterson.markdown
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.walk :as walk]))

(defn headers [lines]
  (->> lines
    (take-while (complement str/blank?))
    (map #(str/split % #": ?"))
    (into {})
    walk/keywordize-keys))

(defn markdown [lines]
  (. (org.pegdown.PegDownProcessor.)
     (markdownToHtml
       (str/join \newline
                 (drop-while (complement str/blank?)
                             lines)))))

(defn parse [path]
  ((juxt headers markdown)
     (line-seq (io/reader path))))
