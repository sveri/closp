(ns {{ns}}.components.components
  (:require
    [com.stuartsierra.component :as component]
    (system.components
      [repl-server :refer [new-repl-server]])
    [{{ns}}.components.selmer :as selm]
    [{{ns}}.components.server :refer [new-web-server]]
    [{{ns}}.components.handler :refer [new-handler]]
    [{{ns}}.components.config :as c]
    [{{ns}}.components.db :refer [new-db]]))


(defn dev-system []
  (component/system-map
    :config (c/new-config (c/prod-conf-or-dev))
    :selmer (selm/new-selmer false)
    :db (component/using (new-db) [:config])
    :handler (component/using (new-handler) [:config :db])
    :web (component/using (new-web-server) [:handler :config])))


(defn prod-system []
  (component/system-map
    :config (c/new-config (c/prod-conf-or-dev))
    :selmer (selm/new-selmer true)
    :db (component/using (new-db) [:config])
    :handler (component/using (new-handler) [:config :db])
    :web (component/using (new-web-server) [:handler :config])))
