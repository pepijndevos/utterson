(ns utterson.mustache
  (:use utterson.compile
        clostache.parser))

(defmethod process "mustache" [f data]
  (let [content (slurp f)]
    (render content data)))
