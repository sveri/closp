(ns {{ns}}.server
  (:require [clojure.java.io :as io]
            [{{ns}}.dev :refer [is-dev? inject-devmode-html browser-repl start-figwheel]]
            [compojure.core :refer [GET defroutes]]
            [compojure.route :refer [resources]]
            [compojure.handler :refer [site]]
            [net.cgrand.enlive-html :refer [deftemplate]]
            [ring.middleware.reload :as reload]
            [environ.core :refer [env]]
            [org.httpkit.server :refer [run-server]]))

(deftemplate page
  (io/resource "templates/index.html") [] [:body] (if is-dev? inject-devmode-html identity))

(defroutes routes
  (resources "/")
  (resources "/react" {:root "react"})
  (GET "/*" req (page)))

(def http-handler
  (if is-dev?
    (reload/wrap-reload (site #'routes))
    (site routes)))

(defn run [& [port]]
  (defonce ^:private server
    (do
      (if is-dev? (start-figwheel))
      (let [port (Integer. (or port (env :port) 10555))]
        (print "Starting web server on port" port ".\n")
        (run-server http-handler {:port port
                          :join? false}))))
  server)

(defn -main [& [port]]
  (run port))
