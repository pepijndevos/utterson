(defproject utterson "0.1"
            :description "Utterson is a static site generator in the style of jekyll & hyde."
            :repositories [["scala-tools" "http://scala-tools.org/repo-releases"]]
            :dependencies [[org.clojure/clojure "1.1.0-master-SNAPSHOT"]
                           [org.clojure/clojure-contrib "1.1.0-master-SNAPSHOT"]
                           [org.markdownj/markdownj "0.3.0-1.0.2b4"]]
            :main utterson.generator)
