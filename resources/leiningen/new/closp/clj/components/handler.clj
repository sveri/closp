(ns {{ns}}.components.handler
  (:require [compojure.core :refer [defroutes routes wrap-routes]]
            [noir.response :refer [redirect]]
            [noir.util.middleware :refer [app-handler]]
            [ring.middleware.defaults :refer [site-defaults]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [ring.middleware.file :refer [wrap-file]]
            [ring.middleware.format :refer [wrap-restful-format]]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [buddy.auth :refer [authenticated?]]
            [buddy.auth.accessrules :refer [wrap-access-rules]]
            [compojure.route :as route]
            [com.stuartsierra.component :as comp]
            [buddy.auth.backends :as backends]
            [{{ns}}.service.auth :as s-auth]
            [{{ns}}.service.config :as s-c]
            [{{ns}}.routes.home :refer [home-routes]]
            [{{ns}}.routes.user :refer [user-routes]]
            [{{ns}}.routes.api :as r-api]
            [{{ns}}.middleware :as m-w :refer [load-middleware]]
            [ring.util.http-response :as resp]))

(defroutes base-routes
           (route/resources "/"))
;(route/not-found "Not Found"))

;; timeout sessions after 30 minutes
(def session-defaults
  {:timeout (* 15 60 30)
   :timeout-response (redirect "/")})

(defn- mk-defaults
  "set to true to enable XSS protection"
  [xss-protection?]
  (-> site-defaults
      (update-in [:session] merge session-defaults)
      (assoc-in [:security :anti-forgery] xss-protection?)))


(defn err-handler [req _]
  (if (get-in req [:headers "authorization"])
    (resp/unauthorized {:error "Unauthorized"})
    (resp/forbidden {:error "Forbidden"})))


(defn jws-backend [config]
  (backends/jws {:secret (:jwt-secret config) :unauthorized-handler err-handler}))


(defn get-handler [config locale {:keys [db]}]
  (routes
    (-> (apply routes [(user-routes config db) (r-api/api-routes db)])
        (wrap-routes #(m-w/add-user % db))
        (wrap-routes wrap-access-rules {:rules s-auth/rest-rules :on-error err-handler})
        (wrap-routes wrap-authorization (jws-backend config))
        (wrap-routes wrap-authentication (jws-backend config))
        (wrap-routes wrap-restful-format :formats [:json-kw :transit-json]))
    (-> (app-handler
          [home-routes base-routes]
          ;; add custom middleware here
          :middleware (load-middleware config)
          :ring-defaults (mk-defaults false))
        ;; add access rules here
        ; Makes static assets in $PROJ
        (wrap-file "resources")
        ; Content-Type, Content-Length, and Last Modified headers for files in body
        (wrap-file-info))))

(defrecord Handler [config locale db]
  comp/Lifecycle
  (start [comp]
    (assoc comp :handler (get-handler (:config config) locale db)))
  (stop [comp] comp))


(defn new-handler []
  (map->Handler {}))