(ns {{ns}}.components.handler
  (:require [compojure.core :refer [defroutes]]
            [noir.response :refer [redirect]]
            [noir.util.middleware :refer [app-handler]]
            [ring.middleware.defaults :refer [site-defaults]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [ring.middleware.file :refer [wrap-file]]
            [compojure.route :as route]
            [com.stuartsierra.component :as comp]
            [{{ns}}.routes.home :refer [home-routes]]
            [{{ns}}.routes.user :refer [user-routes registration-routes]]
            [{{ns}}.middleware :refer [load-middleware]]))

(defroutes base-routes
  (route/resources "/")
  (route/not-found "Not Found"))

;; timeout sessions after 30 minutes
(def session-defaults
  {:timeout (* 60 30)
   :timeout-response (redirect "/")})

(defn- mk-defaults
       "set to true to enable XSS protection"
       [xss-protection?]
       (-> site-defaults
           (update-in [:session] merge session-defaults)
           (assoc-in [:security :anti-forgery] xss-protection?)))

(defn get-handler [config]
  (-> (app-handler
        ;; add your application routes here
        (into [] (concat (when (:self-registration config) [(registration-routes config)])
                         [home-routes (user-routes config) base-routes]))
        ;; add custom middleware here
        :middleware (load-middleware config)
        :ring-defaults (mk-defaults false)
        ;; add access rules here
        :access-rules []
        ;; serialize/deserialize the following data formats
        ;; available formats:
        ;; :json :json-kw :yaml :yaml-kw :edn :yaml-in-html
        :formats [:json-kw :edn :transit-json])
      ; Makes static assets in $PROJECT_DIR/resources/public/ available.
      (wrap-file "resources")
      ; Content-Type, Content-Length, and Last Modified headers for files in body
      (wrap-file-info)))

(defrecord Handler [config]
  comp/Lifecycle
  (start [comp]
    (assoc comp :handler (get-handler (:config config))))
  (stop [comp]
    (assoc comp :handler nil)))

(defn new-handler []
  (map->Handler {}))
