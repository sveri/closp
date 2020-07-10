(ns {{namespace}}.core
  (:require [{{namespace}}.components.components :refer [prod-system]]
            [clojure.tools.logging :as log]
            [system.repl :refer [set-init! start stop]])
  (:gen-class))

(defn -main [& args]
  (set-init! #'prod-system)
  (start)
  (log/info "server started."))

(defn shutdown []
  (log/info "Shutdown hook called, shutting down server")
  (stop))

(.addShutdownHook (Runtime/getRuntime)
                  (Thread. ^Runnable shutdown))
