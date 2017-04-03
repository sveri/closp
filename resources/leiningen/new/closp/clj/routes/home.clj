(ns {{ns}}.routes.home
  (:require [compojure.core :refer [defroutes GET]]
            [{{ns}}.views.home :as vh]
            [ring.util.response :refer [response]]))

(defn home-page [req]
  (vh/home-page req))

(defn contact-page [req]
  (vh/contact-page req))

(defn tos-page [req]
  (vh/tos-page req))

(defn cookies-page [req]
  (vh/cookies-page req))

(defn reagent-example [req]
  (vh/reagent-example req))

(defn ajax-initial-data []
  (response {:ok "fooo" :loaded true}))

(defroutes home-routes
           (GET "/contact" req (contact-page req))
           (GET "/tos" req (tos-page req))
           (GET "/cookies" req (cookies-page req))
           (GET "/" req (home-page req))
           (GET "/reagent-example" req (reagent-example req))
           (GET "/ajax/page/init" [] (ajax-initial-data)))
