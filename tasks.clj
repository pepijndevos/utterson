(ns user
  (:use cake cake.core))

(def index (atom 0))

(deftask love
  "not war"
  (let [text (cycle ["unknown task: love"
                     "You can't hurry love"
                     "Did you mean 'cake war'?\nAdd '--not=war' to ignore this message"
                     "We also love cake!"
                     "Want a peace of cake?"
                     (str "Stop it! You've already wasted #" @index " cakes")])]
    (if (= ["war"] (:not *opts*))
      (println "Hippie!")
      (do
        (println (nth text @index))
        (swap! index inc)))))
