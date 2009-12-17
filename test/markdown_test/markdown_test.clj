(ns pep.markdown
  (:import (org.mozilla.javascript Context ScriptableObject))
  (:use [clojure.contrib.duck-streams :only (spit)]))

(defn showdown [txt]
  (let [cx (Context/enter)
        scope (.initStandardObjects cx)
        input (Context/javaToJS txt scope)
        script (str (slurp "showdown.js")
                    "new Showdown.converter().makeHtml(input);")]
    (try
     (ScriptableObject/putProperty scope "input" input)
     (let [result (.evaluateString cx scope script "<cmd>" 1 nil)]
       (Context/toString result))
     (finally (Context/exit)))))

(defn markdownj [txt]
  (.markdown (new com.petebevin.markdown.MarkdownProcessor) txt))

(let [txt (slurp "showdown.txt")]
  (spit "markdownj.html" (time (markdownj txt)))
  (spit "showdown.html" (time (showdown txt)))
  (.exec (java.lang.Runtime/getRuntime) "opendiff markdownj.html showdown.html"))
