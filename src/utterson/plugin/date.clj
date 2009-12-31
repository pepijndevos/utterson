(ns utterson.plugin.date
  (:import (java.io File))
  (:use utterson.plugin)
  (:use utterson.util))

(defn date [page]
  (if (:date page)
    (let [dest (File. (:dest page))
          url (File. (:url page))
          path (.replaceAll #^String (:date page) "[^0-9]" (File/separator))]
      (assoc page
             :dest (str (.getParent dest) (File/separator)
                        path (File/separator)
                        (.getName dest))
             :url (str (.getParent url) (File/separator)
                        path (File/separator)
                        (.getName url))))
    page))

(register :filter date)
