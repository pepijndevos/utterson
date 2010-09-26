(ns utterson.compile
  (:use utterson.markdown)
  (:require [net.cgrand.enlive-html :as en]
            [clojure.java.io :as io]
            [clojure.string :as st]))

(def path (-> (clojure.lang.DynamicClassLoader.)
            (.getResource "site.clj")
            .getFile
            io/file
            .getParentFile))

(defn- html?
  "Test if file is a html file"
  [file]
  (.endsWith (.getName file) ".html"))

(defn- get-html-files
  "Get all html files from path"
  []
  (filter html?
          (flatten (file-seq path))))

(defn- md->html
  "Convert the given .md File to the appropriate .html File.
  If not an index file, make it filename/index.html
  for pretty links."
  [markdown]
  (if (= (.getName markdown) "index.md")
    (io/file (.getParentFile markdown)
             "index.html")
    (io/file (.getParentFile markdown)
             (first (st/split (.getName markdown) #"\."))
             "index.html")))

(defn- closest
  "Determine the closest file to the given one.
  Closest is defined in this order:
  - itself
  - the most recent file in the same dir
  - the first index file in a parent dir"
  [markdown]
  (let [html (md->html markdown)]
    (if (.exists html)
      html
      (if-let [html (-> html
                      .getParentFile
                      .listFiles
                      seq
                      (->> (sort-by #(.lastModified %))
                           (filter html?))
                      last)]
        html
        (some #(when (.exists (io/file % "index.html"))
                 (io/file % "index.html"))
              (iterate #(.getParentFile %) html))))))

(defn action-or-update
  "Generate idempotent Enlive functions.
  If the selector returns one or more nodes,
  substitute for nodes.
  Else, use action"
  [action selector & nodes]
  (let [action (apply action nodes)]
    #(if (seq (en/select % [(en/has selector)]))
      (en/at % selector (apply en/substitute nodes))
      (action %))))

(def append-or-update (partial action-or-update en/append))

(def prepend-or-update (partial action-or-update en/prepend))

(defmacro chain-template
  "Like snippet* but for our own evil purposes"
  [rec expr]
  `(en/snippet* (en/html-resource ~rec) [~'headers ~'body] ~@expr))

(defn- update-all
  "Updates all html pages for every page given as
  [headers body expressions]
  but save on IO as much as possible."
  [pages]
  (doseq [file (get-html-files)]
    (reduce (fn [html page]
              (-> (chain-template
                    html
                    (last page))
                (apply (drop-last page))
                (->>
                  (apply str)
                  (spit file))))
            file pages)))

(defn- generate
  "Run template with the Markdown-parsed file and expressions in self.
  if template is nil, closest is used."
  [template file self]
  (let [template (if-not template
                   (closest file)
                   template)
        template (chain-template template self)
        file (io/file path file)
        html (md->html file)
        rel-html (.relativize (.toURI path) (.toURI html))
        [headers body] (parse file)
        headers (assoc headers# :path (.getPath rel-html))]
    (-> html
      io/make-parents
      (spit (->> (template headers body)
                 en/emit*
                 (apply str))))
    [headers body]))
        
(defn generator
  "Generate and update all html files using template or closest.
  files is a seq of Markdown files.
  self and others are Enlive expressions for generating files
  and updating all other files respecively."
  [template files self others]
  (-> (map #(conj (generate template % self) others) files)
    update-all))
    

(defmacro defgen
  "Like Enlives deftemplate, with source and args provided.
  args is always [headers body]
  forms are given in 2 seqs.
  The first one for generating pages.
  The second one for updating references"
  [template-name self others]
  `(defn ~template-name [template# files#]
    (generator template# files# ~self ~others)))
