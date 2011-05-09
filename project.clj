(defproject utterson "0.1"
            :description "Utterson is a static site generator in the style of jekyll & hyde."
            :dependencies [[org.clojure/clojure "1.2.1"]
                           [org.pegdown/pegdown "0.9.2"]
                           [enlive "1.0.0"]
                           [ring "0.3.8"]
                           [seqex "1.0.0-SNAPSHOT"]]
            :repositories {"scala-tools.org" "http://scala-tools.org/repo-releases/"}
            :main utterson.main)
