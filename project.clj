(defproject utterson "0.1"
            :description "Utterson is a static site generator in the style of jekyll & hyde."
            :dependencies [[org.clojure/clojure "1.2.0"]
                           [org.clojure/clojure-contrib "1.2.0"]
                           [org.clojars.mcav/pegdown "0.8.5.1"]
                           [enlive "1.0.0-SNAPSHOT"]]
            :dev-dependencies [[org.clojars.brandonw/lein-nailgun "1.0.0"]]
            :main utterson.main)
