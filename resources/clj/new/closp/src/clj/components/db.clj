(ns {{namespace}}.components.db
  (:require [com.stuartsierra.component :as component]))

(defrecord Db [config]
  component/Lifecycle
  (start [component]
    (let [db-conn {:connection-uri (or (System/getenv "DATABASE_URL") (get-in config [:config :jdbc-url]))}]
      (assoc component :db db-conn)))
  (stop [component]
    (when-let [conn-pool (:db component)] (.close (:datasource conn-pool)))
    (assoc component :db nil)))

(defn new-db []
  (map->Db {}))
