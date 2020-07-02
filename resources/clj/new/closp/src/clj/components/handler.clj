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


(defn add-locale [handler dev?]
  (fn [req]
    (let [accept-language (get-in req [:headers "accept-language"])
          short-languages (if dev? ["en"] (or (tr/parse-http-accept-header accept-language) ["en"]))]
      (handler (assoc req :localize (partial tr/tr
                                             {:default-locale :en
                                              :dict           locale-dict}
                                             short-languages))))))

; use different middleware for rest api
;(defn wrap-api [route db dev?]
;  (let [handler (-> route
;                    (wrap-json-response)
;                    (add-locale dev?)
;                    (wrap-custom-authorization db)
;                    (wrap-json-body {:keywords? true :bigdecimals? true}))]
;    (if dev? (wrap-reload handler) handler)))

(defn wrap-base [route dev?]
  (let [handler (-> route
                    (add-locale dev?)
                    (wrap-access-rules {:rules auth/rules})
                    (wrap-authorization auth/auth-backend)
                    (wrap-defaults site-defaults))]
    (if dev? (wrap-reload handler) handler)))

(defn get-handler [config {:keys [db]}]
  (let [dev? (= (:env config "") :dev)]
    (routes
      ;(wrap-api (api-routes db) db dev?)
      (wrap-base
        (routes (home-routes) (user-routes config db))
        dev?))))

(defrecord Handler [config db]
  comp/Lifecycle
  (start [comp]
    (assoc comp :handler (get-handler (:config config) db)))
  (stop [comp] comp))


(defn new-handler []
  (map->Handler {}))
