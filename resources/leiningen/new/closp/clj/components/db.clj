(ns {{ns}}.components.db
  (:require [mount.core :refer [defstate]]
            [korma.db :as korma]
            [{{ns}}.components.config :refer [config]]))

(defn new-db [config]
  (let [db (korma/create-db (get-in config [:jdbc-url]))]
    (korma/default-connection db)))

(defstate db :start (new-db config))
