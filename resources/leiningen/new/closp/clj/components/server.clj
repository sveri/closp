(ns {{ns}}.components.server
  (:require [immutant.web :as web]
            [com.stuartsierra.component :as component]))

(defrecord WebServer [handler config]
  component/Lifecycle
  (start [component]
    (let [handler (:handler handler)
          port (get-in config [:config :port] 3000)
          server (if (= (:env config) :dev)
                   (web/run-dmc handler {:port port})
                   (web/run handler {:port port :host "0.0.0.0"}))]
      (assoc component :server server)))
  (stop [component]
    (when-let [server (:server component)]
      (web/stop server))
    component))

(defn new-web-server []
  (map->WebServer {}))
