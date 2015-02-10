(ns {{ns}}.components.components
  (:require
    [com.stuartsierra.component :as component]
    (system.components
      [repl-server :refer [new-repl-server]])
    [{{ns}}.components.server :refer [new-web-server new-web-server-prod]]
    [{{ns}}.components.handler :refer [new-handler]]
    [{{ns}}.components.config :refer [new-config]]
    [{{ns}}.components.db :refer [new-db]]))


(defn dev-system []
  (component/system-map
    :config (new-config)
    :db (component/using (new-db) [:config])
    :handler (component/using (new-handler) [:config])
    :web (component/using (new-web-server) [:handler :config])))


(defn prod-system []
  (component/system-map
    :config (new-config)
    :db (component/using (new-db) [:config])
    :handler (component/using (new-handler) [:config])
    :web (component/using (new-web-server-prod) [:handler :config])))
