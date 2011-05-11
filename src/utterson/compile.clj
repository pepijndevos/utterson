(ns utterson.compile
  (:import org.apache.commons.io.FileUtils)
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(def ^:dynamic *dist* "dist/")

(defn files
  ([] (files (io/file ".")))
  ([path]
   (filter
     #(and (.isFile %) (not (.isHidden %)))
     (file-seq path))))

(defn extension [path]
  (second (re-find #"\.([a-z]+)$" (.getName (io/file path)))))

(defmulti process extension)

(defmethod process :default [path]
  (FileUtils/copyFile (io/file path) (io/file *dist* path)))

(defn process-all [path order]
  (let [fs (files (io/file path))
        exts (group-by extension fs)]
    (reduce conj
            {}
            (for [ext order
                  file (get exts ext)]
              (process file)))))
