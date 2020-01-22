(ns {{namespace}}.components.db
  (:require [com.stuartsierra.component :as component])
  (:import (com.mchange.v2.c3p0 ComboPooledDataSource)))

(defn pool [config]
  (let [db-url (get-in config [:config :jdbc-url])
        cpds (doto (ComboPooledDataSource.)
               (.setDriverClass (get-in config [:config :jdbc-driver-class config]))
               (.setJdbcUrl db-url)
               ;; expire excess connections after 30 minutes of inactivity:
               (.setMaxIdleTimeExcessConnections (* 30 60))
               ;; expire connections after 3 hours of inactivity:
               (.setMaxIdleTime (* 3 60 60)))]
    {:datasource cpds}))

(defrecord Db [config]
  component/Lifecycle
  (start [component]
    (let [conn-pool (pool config)]
      (assoc component :db conn-pool)))
  (stop [component]
    (when-let [conn-pool (:db component)] (.close (:datasource conn-pool)))
    (assoc component :db nil)))

(defn new-db []
  (map->Db {}))
