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

(defn- html? [file]
  (.endsWith (.getName file) ".html"))

(defn- get-html-files []
  (filter html?
          (flatten (file-seq path))))

(defn- md->html [markdown]
  (if (= (.getName markdown) "index.md")
    (io/file (.getParentFile markdown)
             "index.html")
    (io/file (.getParentFile markdown)
             (first (st/split (.getName markdown) #"\."))
             "index.html")))

(defn- closest [markdown]
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
  [action selector & nodes]
  (let [action (apply action nodes)]
    #(if (seq (en/select % [(en/has selector)]))
      (en/at % selector (apply en/substitute nodes))
      (action %))))

(def append-or-update (partial action-or-update en/append))

(def prepend-or-update (partial action-or-update en/prepend))

(defmacro chain-template [rec expr]
  `(en/snippet* ~(en/html-resource rec) [~'headers ~'body] ~@expr))

(defn- update-all [pages]
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

(defn- generate [template file self]
  (let [template (if-not template
                   (closest file)
                   template)
        template (chain-template template [headers body] self)
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
        
(defn generator [template files self others]
  (-> (map #(conj (generate template % self) others) files)
    update-all))
    

(defmacro defgen [template-name self others]
  `(defn ~template-name [template# files#]
    (generator template# files# ~self ~others)))

(comment (defmacro defgen
  "Like deftemplate in Enlive,
  but with 2 sets of selectors.
  One for creating the page,
  the other for updating information elsewere.
  The locals body and headers are exposed and contain
  the parsed Markdown and headers(title, tags...) respectively."
  [template-name self others]
  `(defn ~template-name [markdown# template#]
     (let [markdown# (io/file path markdown#)
           html# (md->html markdown#)
           rel-html# (.relativize (.toURI path) (.toURI html#))
           [headers# body#] (parse markdown#)
           headers# (assoc headers# :path (.getPath rel-html#))
           template# (if-not template#
                       (closest markdown#)
                       template#)
           template# (en/template template# [~'headers ~'body] ~@self)]
       (io/make-parents html#)
       (spit html# (apply str (template# headers# body#)))
       (doseq [file# (get-html-files)
               :let [template# (en/template file# [~'headers ~'body] ~@others)]]
         (spit file# (apply str (template# headers# body#))))))))
