(ns {{namespace}}.components.handler
  (:require [compojure.core :refer [defroutes routes wrap-routes]]
            [compojure.handler :refer [site]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.flash :refer [wrap-flash]]
            [compojure.route :as route]
            [com.stuartsierra.component :as comp]
            [taoensso.tempura :as tr]
            [buddy.auth.middleware :refer [wrap-authorization]]
            [buddy.auth.accessrules :refer [wrap-access-rules]]
            [{{namespace}}.service.auth :as auth]
            [{{namespace}}.routes.home :refer [home-routes]]
            [{{namespace}}.routes.user :refer [user-routes]]))

(defroutes base-routes
           (route/resources "/")
           (route/not-found "Not Found"))

(def locale-dict
  {:de {:__load-resource "i18n/de.edn"}
   :en {:__load-resource "i18n/en.edn"}})


(defn add-locale [handler]
  (fn [req]
    (let [accept-language (get-in req [:headers "accept-language"])
          short-languages (or (tr/parse-http-accept-header accept-language) ["en"])]
      (handler (assoc req :localize (partial tr/tr
                                             {:default-locale :en
                                              :dict           locale-dict}
                                             short-languages))))))


(defn wrap-base [route dev?]
  (let [handler (-> (route)
                    (wrap-access-rules {:rules auth/rules})
                    (wrap-authorization auth/auth-backend)
                    add-locale
                    (wrap-defaults site-defaults))]
    (if dev? (wrap-reload handler) handler)))

(defn get-handler [config {:keys [db]}]
  (let [dev? (= (:env config "") :dev)]
    (routes
      (wrap-base home-routes dev?)
      (wrap-base (partial user-routes config db) dev?)))


  #_(routes (-> (#'home-routes)
                (wrap-routes wrap-reload)                   ;TODO only when dev env
                (wrap-defaults site-defaults)
                (wrap-routes add-locale))
            base-routes))
;(routes (-> (#'home-routes)
;            (wrap-routes wrap-reload) ;TODO only when dev env
;            (wrap-defaults site-defaults)
;            (wrap-routes add-locale))
;        base-routes))

;(routes
;(-> (ws-routes websockets)
;    (wrap-routes wrap-params)
;    (wrap-routes wrap-keyword-params))
;(wrap-routes wrap-anti-forgery))
;(-> (app-handler
;      []
;      ;(into [] (concat (when (:registration-allowed? config) [(registration-routes config db)])
;      ;                 ;; add your application routes here
;      ;                 [home-routes (user-routes config db) base-routes]))
;      ;; add custom middleware here
;      ;:middleware (load-middleware config)
;      ;:ring-defaults (mk-defaults false)
;      ;; add access rules here
;      :access-rules []
;      ;; serialize/deserialize the following data formats
;      ;; available formats:
;      ;; :json :json-kw :yaml :yaml-kw :edn :yaml-in-html
;      :formats [:json-kw :edn :transit-json])
;    ; Makes static assets in $PROJECT_DIR/resources/public/ available.
;    (wrap-file "resources")
;    ; Content-Type, Content-Length, and Last Modified headers for files in body
;    (wrap-file-info)))

(defrecord Handler [config db]
  comp/Lifecycle
  (start [comp]
    (assoc comp :handler (get-handler (:config config) db)))
  (stop [comp] comp))


(defn new-handler []
  (map->Handler {}))
