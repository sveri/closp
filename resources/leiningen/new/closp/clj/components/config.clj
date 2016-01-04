(ns {{ns}}.components.config
  (:require [com.stuartsierra.component :as component]
            [nomad :refer [read-config]]
            [clojure.java.io :as io]))

(defn prod-conf-or-dev []
  (if-let [config-path (System/getProperty "closp-config-path")]
    (read-config (io/file config-path))
    (read-config (io/resource "closp.edn"))))

(defrecord Config [config]
  component/Lifecycle
  (start [component]
    (assoc component :config config))
  (stop [component]
    (assoc component :config nil)))

(defn new-config [config]
  (->Config config))