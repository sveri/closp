(ns {{ns}}.service.config
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]))

(defn prod-conf-or-dev []
  (edn/read-string
    (slurp (if-let [config-path (System/getProperty "closp-config-path")]
             (io/file config-path)
             (io/resource "closp.edn")))))
