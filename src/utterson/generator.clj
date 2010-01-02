(ns utterson.generator
  (:gen-class)
  (:use compojure)
  (:use utterson.core))

(defn get-single [url src dest]
  (time (some #(when (= url (:url %)) %) (template (reader src dest)))))

(defn serve [pages src dest]
  (run-server {:port 8080} "/*" 
              (servlet
                (apply compojure.http.routes/routes
                       (GET "/" "Hello World!")
                       (map #(GET (:url %) (force (:body (get-single (:url %) src dest)))) pages)))))

(defn -main [& args]
  (let [pages (time (template (reader (last (butlast args)) (last args))))]
    (if (= (first args) "--server")
      (serve pages (last (butlast args)) (last args))
      (time (writer pages)))))

