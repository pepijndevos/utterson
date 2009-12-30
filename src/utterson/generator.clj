(ns utterson.generator
  (:gen-class)
  (:use compojure)
  (:use utterson.core))

(defn get-single [url src dest]
  (some #(when (= url (:url (last %))) %) (template (reader src dest))))

(defn serve [pages src dest]
  (run-server {:port 8080} "/*" 
              (servlet
                (apply compojure.http.routes/routes
                       (GET "/" "Hello World!")
                       (map #(GET (:url %) (:body (get-single (:url %) src dest))) pages)))))

(defn -main [& args]
  (let [pages (template (reader (last (butlast args)) (last args)))]
    (if (= (first args) "--server")
      (serve pages (last (butlast args)) (last args))
      (writer pages))))

