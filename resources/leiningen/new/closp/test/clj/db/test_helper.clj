(ns {{ns}}.db.test-helper
  (:require [clojure.test :refer :all]
            [{{ns}}db.test-helper :as th])
  (:import (com.mchange.v2.c3p0 ComboPooledDataSource)))


(defn pool [config]
  (let [db-url (get-in config [:jdbc-url])
        cpds (doto (ComboPooledDataSource.)
               (.setDriverClass (get-in config [:jdbc-driver-class config]))
               (.setJdbcUrl db-url)
               ;; expire excess connections after 30 minutes of inactivity:
               (.setMaxIdleTimeExcessConnections (* 30 60))
               ;; expire connections after 3 hours of inactivity:
               (.setMaxIdleTime (* 3 60 60)))]
    {:datasource cpds}))


(def db (pool {:jdbc-url              "jdbc:postgresql://localhost:5432/{{name}}_test?user={{name}}&password={{name}}"
               :jdbc-driver-class     "org.postgresql.Driver"}))
