(ns utterson.generator
  (:gen-class)
  (:use compojure)
  (:use utterson.core))

(defn get-pages []
  (let [pages (reader)]
    (await pages)
    (map #(template % @pages) @pages)))

(defn get-single [url]
  (some #(when (= url (:url (last %))) %) (get-pages)))

(defn serve [pages]
  (run-server {:port 8080} "/*" 
              (servlet
                (apply compojure.http.routes/routes
                       (GET "/" "Hello World!")
                       (map #(GET (:url (last %)) (get-single (:url (last %)))) pages)))))

(defn -main [& args]
  (init (last ( butlast args)) (last args))
  (let [pages (get-pages)]
    (if (= (first args) "--server")
      (serve pages)
      (do (writer pages) (shutdown-agents)))))

