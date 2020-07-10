(ns {{namespace}}.components.components
  (:require [clojure.tools.logging :as log]
            [com.stuartsierra.component :as component]
            [{{namespace}}.components.server :refer [new-web-server]]
            [{{namespace}}.components.handler :refer [new-handler]]
            [{{namespace}}.components.config :as c]
            [{{namespace}}.components.db :refer [new-db]]))

; JVM wide handler for uncaught exceptions from different threads
(Thread/setDefaultUncaughtExceptionHandler
  (reify Thread$UncaughtExceptionHandler
    (uncaughtException [_ thread ex]
      (log/error ex "Uncaught exception on" (.getName thread)))))

(defn dev-system []
  (component/system-map
    :config (c/new-config (c/prod-conf-or-dev))
    :db (component/using (new-db) [:config])
    :handler (component/using (new-handler) [:config :db])
    :web (component/using (new-web-server) [:handler :config])))


(defn prod-system []
  (component/system-map
    :config (c/new-config (c/prod-conf-or-dev))
    :db (component/using (new-db) [:config])
    :handler (component/using (new-handler) [:config :db])
    :web (component/using (new-web-server) [:handler :config])))
