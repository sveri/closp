(ns {{ns}}.components.handler
  (:require [compojure.core :refer [defroutes]]
            [noir.response :refer [redirect]]
            [noir.util.middleware :refer [app-handler]]
            [ring.middleware.defaults :refer [site-defaults]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [ring.middleware.file :refer [wrap-file]]
            [compojure.route :as route]
            [mount.core :refer [defstate]]
            [{{ns}}.components.config :refer [config]]
            [{{ns}}.components.locale :refer [get-tconfig]]
            [{{ns}}.routes.home :refer [home-routes]]
            [{{ns}}.routes.cc :refer [cc-routes]]
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

(defn get-handler [config locale]
  (-> (app-handler
        (into [] (concat (when (:registration-allowed? config) [(registration-routes config)])
                         ;; add your application routes here
                         [(cc-routes config) home-routes (user-routes config) base-routes]))
        ;; add custom middleware here
        :middleware (load-middleware config (:tconfig locale))
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

(defstate handler :start (get-handler config (get-tconfig)))
