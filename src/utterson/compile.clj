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

(defmethod process :default [path]) ; do nothing

(defmulti generate (fn [file data] (extension file)))

(defmethod generate :default [path]
  (FileUtils/copyFile (io/file path) (io/file *dist* path)))

(defn process-all [path]
  (into {} (map process (files (io/file path)))))

(defn route [fs router]
  (apply merge-with into
         (for [[file data] fs]
           (into {} (map #(vector % [data]) (router file))))))

(defn generate-all [path settings data router]
  (let [data (route data router)]
    (doseq [file (files (io/file path))
            :let [data (get data (.getPath file))]]
      (println file data))))
