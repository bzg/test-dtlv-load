#!/usr/bin/env bb

;; Require pods and datalevin pod
(require '[babashka.pods :as pods])
(require '[babashka.cli :as cli])
(pods/load-pod 'huahaiy/datalevin "0.8.2")
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
;; (d/transact! conn (take 7555 all)) ;; Works okay
;; (d/transact! conn (take 7556 all)) ;; Fails systematically

;; The culprit seems to be this entry, for which there is nothing
;; wrong:

;; {"id" : 648, "name" : "txm-manual", "organization_name" : "txm", "platform" : "GitLab", "repository_url" : "https://gitlab.huma-num.fr/txm/txm-manual", "description" : "TXM User's Manual writing and online hosting", "default_branch" : "master", "is_fork" : "", "is_archived" : false, "creation_date" : "2020-04-14T14:49:37Z", "last_update" : "2022-05-03T15:27:53Z", "last_modification" : "2022-05-03T15:27:53Z", "homepage" : "", "stars_count" : 0, "forks_count" : 0, "license" : "", "open_issues_count" : 0, "language" : "", "topics" : "", "software_heritage_exists" : false, "software_heritage_url" : ""}

(d/transact! conn all)

(comment
  (count (d/q '[:find ?a :where [?a :id _]] db)))
