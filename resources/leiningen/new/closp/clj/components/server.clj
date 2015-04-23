(ns {{ns}}.components.server
  (:require [com.stuartsierra.component :as component]
            [taoensso.timbre :as timbre]
            [ring.server.standalone :refer [serve]]
            [org.httpkit.server :refer [run-server]]
            [cronj.core :as cronj]
            [taoensso.timbre.appenders.rotor :as rotor]
            [selmer.parser :as parser]
            [{{ns}}.session :as session]))

(defn destroy
  "destroy will be called when your application
   shuts down, put any clean up code here"
  []
  (timbre/info "{{name}} is shutting down...")
  (cronj/shutdown! session/cleanup-job)
  (timbre/info "shutdown complete!"))

(defn init
  "init will be called once when
   app is deployed as a servlet on
   an app server such as Tomcat
   put any initialization code here"
  [config]
  (timbre/set-config!
    [:appenders :rotor]
    {:min-level :info
     :enabled? true
     :async? false ; should be always false for rotor
     :max-message-per-msecs nil
     :fn rotor/appender-fn})

  (timbre/set-config!
    [:shared-appender-config :rotor]
    {:path "{{name}}" :max-size (* 512 1024) :backlog 10})

  (when (= (:env config) :dev) (parser/cache-off!))
  ;;start the expired session cleanup job
  (cronj/start! session/cleanup-job)
  (timbre/info "\n-=[ {{name}} started successfully"
               (when (= (:env config) :dev) "using the development profile") "]=-"))

(defrecord WebServer [handler config]
  component/Lifecycle
  (start [component]
    (let [handler (:handler handler)
          config (:config config)
          server (serve handler
                        {:port         3000
                         :init         (partial init config)
                         :auto-reload? true
                         :destroy      destroy
                         :join?        false
                         :open-browser? false})]
      (assoc component :server server)))
  (stop [component]
    (let [server (:server component)]
      (when server (.stop server)))
    component))

(defn new-web-server []
  (map->WebServer {}))

(defrecord WebServerProd [handler config]
  component/Lifecycle
  (start [component]
    (let [handler (:handler handler)
          server (run-server handler {:port 3000})]
      (assoc component :server server)))
  (stop [component]
    (let [server (:server component)]
      (when server (server)))
    component))

(defn new-web-server-prod []
  (map->WebServerProd {}))
