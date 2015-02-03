(ns {{ns}}.globals
  (:require [nomad :refer [defconfig]]
            [clojure.java.io :as io]))

(defconfig closp-config (io/resource "closp.edn"))

(def ^:const hostname (:hostname (closp-config)))
(def ^:const mail-from (:mail-from (closp-config)))
(def ^:const mail-type (:mail-type (closp-config)))
(def ^:const activation-mail-subject (:activation-mail-subject (closp-config)))
(def ^:const activation-mail-body (:activation-mail-body (closp-config)))
(def ^:const activation-placeholder (:activation-placeholder (closp-config)))
(def ^:const smtp-data (:smtp-data (closp-config))) ; passed directly to postmap like {:host "postfix"}
(def ^:const jdbc-url (:jdbc-url (closp-config)))
(def ^:const env (:env (closp-config)))