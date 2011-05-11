(ns utterson.markdown
  (:use utterson.compile)
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.walk :as walk]))

(defn variables [r]
  (->> r
    io/reader
    line-seq
    (take-while (complement str/blank?))
    (map #(str/split % #": ?"))
    (into {})
    walk/keywordize-keys))

(defn markdown [r]
  (. (org.pegdown.PegDownProcessor.)
     (markdownToHtml (slurp r))))

(defmethod process "md" [f]
  (let [r (io/reader f)]
    [(.getPath (io/file f))
     [(variables r) (markdown r)]]))


