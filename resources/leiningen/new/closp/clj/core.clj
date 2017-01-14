(ns {{ns}}.core
  (:require [clojure.tools.logging :as log]
            [{{ns}}.cljccore :as cljc]
            [{{ns}}.components.components :refer [prod-system]]
            [com.stuartsierra.component :as component])
  (:gen-class))

(defn -main [& args]
  (alter-var-root #'prod-system component/start)
  (log/info "server started."))
