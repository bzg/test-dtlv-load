#!/usr/bin/env bb

;; Require pods and datalevin pod
(require '[babashka.pods :as pods])
(require '[babashka.cli :as cli])
(pods/load-pod 'huahaiy/datalevin "0.8.0")
(require '[pod.huahaiy.datalevin :as d])
(require '[cheshire.core :as json])

;; Create the database
(def schema {:id          {:db/valueType :db.type/long
                           :db/unique    :db.unique/identity}
             :description {:db/valueType :db.type/string
                           :db/fulltext  true}})
(def conn (d/get-conn "/tmp/test-dtlv" schema))
(def db (d/db conn))

;; Parse the json
(def all  (json/parse-string (slurp "all.json") true))

;; Feed the db
(d/transact! conn all)

(comment
  (count (d/q '[:find ?a :where [?a :id _]] db)))
