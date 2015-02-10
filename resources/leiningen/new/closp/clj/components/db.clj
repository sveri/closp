(ns {{ns}}.components.db
  (:require [com.stuartsierra.component :as component]
            [korma.db :refer [defdb]]))

(defrecord Db [config]
  component/Lifecycle
  (start [component]
    (let [db-url (get-in config [:config :jdbc-url])]
      (defdb db db-url))
    component)
  (stop [component] component))

(defn new-db []
  (map->Db {}))
