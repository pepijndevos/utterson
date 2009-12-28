(ns utterson.generator
  (:gen-class)
  (:use compojure)
  (:use utterson.core))

(defn get-pages [src dest]
  (let [pages (reader src dest)]
    (await pages)
    (map #(template % @pages) @pages)))

(defn get-single [url src dest]
  (some #(when (= url (:url (last %))) %) (get-pages src dest)))

(defn serve [pages src dest]
  (run-server {:port 8080} "/*" 
              (servlet
                (apply compojure.http.routes/routes
                       (GET "/" "Hello World!")
                       (map #(GET (:url (last %)) (get-single (:url (last %)) src dest)) pages)))))

(defn -main [& args]
  (let [pages (get-pages (last ( butlast args)) (last args))]
    (if (= (first args) "--server")
      (serve pages (last ( butlast args)) (last args))
      (do (writer pages) (shutdown-agents)))))

