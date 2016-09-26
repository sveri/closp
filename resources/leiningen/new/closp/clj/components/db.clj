(ns {{ns}}.components.db
  (:require [com.stuartsierra.component :as component]))

(defrecord Db [config]
  component/Lifecycle
  (start [component]
    (let [db-url (get-in config [:config :jdbc-url])]
      (assoc component :db {:connection-uri db-url})))
  (stop [component] component))

(defn new-db []
  (map->Db {}))
