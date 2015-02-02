(ns {{ns}}.globals
  (:require [de.sveri.clojure.commons.files.edn :as commons]))
(def ^:const closp-config-name "closp.edn")

(def ^:const closp-config (commons/from-edn closp-config-name))

(def ^:const hostname (:hostname closp-config))
(def ^:const mail-from (:mail-from closp-config))
(def ^:const mail-type (:mail-type closp-config))
(def ^:const activation-mail-subject (:activation-mail-subject closp-config))
(def ^:const activation-mail-body (:activation-mail-body closp-config))
(def ^:const activation-placeholder (:activation-placeholder closp-config))
(def ^:const smtp-data (:smtp-data closp-config)) ; passed directly to postmap like {:host "postfix"}
(def ^:const jdbc-url (:jdbc-url closp-config))