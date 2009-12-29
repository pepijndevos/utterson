(defn post [a b]
  (assoc a :body (str "this is Tydiness.clj" \newline (:body a))))
