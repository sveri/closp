(ns {{namespace}}.components.config
  (:require [com.stuartsierra.component :as component]
            [clojure.edn :as edn]
            [clojure.java.io :as io]))

(defn prod-conf-or-dev []
  (edn/read-string
    (slurp (if-let [config-path (System/getProperty "closp-config-path")]
             (io/file config-path)
             (io/resource "closp.edn")))))

(defrecord Config [config]
  component/Lifecycle
  (start [component]
    (assoc component :config config))
  (stop [component] component))


(defn new-config [config]
  (->Config config))
