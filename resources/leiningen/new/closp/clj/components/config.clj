(ns {{ns}}.components.config
  (:require [com.stuartsierra.component :as component]
            [nomad :refer [read-config]]
            [clojure.java.io :as io]))

(defn prod-conf-or-dev []
  (if-let [config-path (System/getProperty "closp-config-path")]
    (read-config (io/file config-path))
    (read-config (io/resource "closp.edn"))))

(defrecord Config []
  component/Lifecycle
  (start [component]
    (let [closp-config (prod-conf-or-dev)]
      (assoc component :config {:hostname (:hostname closp-config)
                                :mail-from (:mail-from closp-config)
                                :mail-type (:mail-type closp-config)
                                :activation-mail-subject (:activation-mail-subject closp-config)
                                :activation-mail-body (:activation-mail-body closp-config)
                                :activation-placeholder (:activation-placeholder closp-config)
                                :smtp-data (:smtp-data closp-config) ; passed directly to postmap like {:host "postfix"}
                                :jdbc-url (:jdbc-url closp-config)
                                :env (:env closp-config)})))
  (stop [component]
    (assoc component :config nil)))

(defn new-config []
  (->Config ))
