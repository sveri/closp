(ns {{ns}}.components.server
  (:require [com.stuartsierra.component :as component]
            [clojure.tools.logging :as log]
            [org.httpkit.server :refer [run-server]]
            [hara.io.scheduler :as sched]
            [selmer.parser :as parser]
            [{{ns}}.session :as session])
  (:import (clojure.lang AFunction)))

(defn destroy
  "destroy will be called when your application
   shuts down, put any clean up code here"
  []
  (log/info "{{name}} is shutting down...")
  (sched/shutdown! session/cleanup-job)
  (log/info "shutdown complete!"))

(defn init
  "init will be called once when
   app is deployed as a servlet on
   an app server such as Tomcat
   put any initialization code here"
  [config]
  (when (= (:env config) :dev) (parser/cache-off!))
  ;;start the expired session cleanup job
  (sched/start! session/cleanup-job)
  (log/info "\n-=[ {{name}} started successfully"
               (when (= (:env config) :dev) "using the development profile") "]=-"))

(defrecord WebServer [handler config]
  component/Lifecycle
  (start [component]
    (let [handler (:handler handler)
          server (run-server handler {:port (get-in config [:config :port] 3000)})]
      (assoc component :server server)))
  (stop [component]
    (let [server (:server component)]
      (when server (server)))
    component))

(defn new-web-server []
  (map->WebServer {}))
