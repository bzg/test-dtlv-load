#!/usr/bin/env bb

;; Require pods and datalevin pod
(require '[babashka.pods :as pods])
(require '[babashka.cli :as cli])
(pods/load-pod 'huahaiy/datalevin "0.8.0")
(require '[pod.huahaiy.datalevin :as d])

;; Create the database
(def schema {:id          {:db/valueType :db.type/string
                           :db/unique    :db.unique/identity}
             :description {:db/valueType :db.type/string
                           :db/fulltext  true}})
(def conn (d/get-conn "/tmp/test-dtlv" schema))
(def db (d/db conn))

;; Get the data
(def dtg-raw-data (csv/read-csv (slurp "data.csv")))

;; Return data rows as maps
(defn- rows->maps [csv]
  (let [headers (map keyword (first csv))
        rows    (rest csv)]
    (map #(zipmap headers %) rows)))

;; Add data to the db
(d/transact! conn (rows->maps dtg-raw-data))

(comment
  (count (d/q '[:find ?a :where [?a :id _]] db)))
