(ns {{ns}}.components.server
  (:require [org.httpkit.server :as hkit]
    [com.stuartsierra.component :as component]
    [clojure.tools.logging :as log]))

(defrecord WebServer [handler config]
  component/Lifecycle
  (start [component]
    (let [handler (:handler handler)
          port (get-in config [:config :port] 3000)
          server (hkit/run-server handler
                                  {:port         port
                                   :error-logger (fn [msg ex]
                                                   (log/error msg)
                                                   (log/error (-> ex Throwable->map clojure.main/ex-triage clojure.main/ex-str))
                                                   (log/error ex))})]
      (log/info "added server to component")
      (assoc component :server server)))
  (stop [component]
    (when-let [server (:server component)]
      (log/info "stopping server")
      (server))
    (assoc component :server nil)))

(defn new-web-server []
  (map->WebServer {}))
