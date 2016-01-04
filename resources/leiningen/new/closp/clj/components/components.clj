(ns foo.bar.components.components
  (:require
    [com.stuartsierra.component :as component]
    (system.components
      [repl-server :refer [new-repl-server]])
    [foo.bar.components.server :refer [new-web-server]]
    [foo.bar.components.handler :refer [new-handler]]
    [foo.bar.components.config :as c]
    [foo.bar.components.db :refer [new-db]]
    [foo.bar.components.locale :as l]))


(defn dev-system []
  (component/system-map
    :locale (l/new-locale)
    :config (c/new-config (c/prod-conf-or-dev))
    :db (component/using (new-db) [:config])
    :handler (component/using (new-handler) [:config :locale])
    :web (component/using (new-web-server) [:handler :config])))


(defn prod-system []
  (component/system-map
    :locale (l/new-locale)
    :config (c/new-config (c/prod-conf-or-dev))
    :db (component/using (new-db) [:config])
    :handler (component/using (new-handler) [:config :locale])
    :web (component/using (new-web-server) [:handler :config])))
