(ns {{ns}}.components.config
  (:require [mount.core :refer [defstate]]
    [nomad :refer [read-config]]
    [clojure.java.io :as io]))

(defn prod-conf-or-dev []
  (if-let [config-path (System/getProperty "closp-config-path")]
    (read-config (io/file config-path))
    (read-config (io/resource "closp.edn"))))

(defstate config :start (prod-conf-or-dev)
          :stop :stopped)