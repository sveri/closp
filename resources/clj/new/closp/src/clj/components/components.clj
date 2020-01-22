(ns {{namespace}}.components.components
  (:require
    [com.stuartsierra.component :as component]
    [{{namespace}}.components.server :refer [new-web-server]]
    [{{namespace}}.components.handler :refer [new-handler]]
    [{{namespace}}.components.config :as c]
    [{{namespace}}.components.db :refer [new-db]]))


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
