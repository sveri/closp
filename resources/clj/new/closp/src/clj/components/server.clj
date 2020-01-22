(ns {{namespace}}.components.server
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
                                                   ;(log/error (-> ex Throwable->map clojure.main/ex-triage clojure.main/ex-str))
                                                   (log/error ex))})]
      (if (= :dev (get-in config [:config :env] ""))
        (log/info "Starting server in dev mode on port: " port)
        (log/info "Starting server in prod mode on port: " port))
      (assoc component :server server)))
  (stop [component]
    (when-let [server (:server component)]
      (server))
    (assoc component :server nil)))

(defn new-web-server []
  (map->WebServer {}))
