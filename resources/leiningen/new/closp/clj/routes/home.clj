(ns {{ns}}.routes.home
  (:require [compojure.core :refer [defroutes GET]]
            [{{ns}}.layout :as layout]))

(defn home-page []
  (layout/render "home/index.html"))

(defn contact-page []
  (layout/render "home/contact.html"))

(defn tos-page []
  (layout/render "home/tos.html"))

(defn cookies-page []
  (layout/render "home/cookies.html"))

(defn example-page []
  (layout/render "home/example.html"))

(defn ajax-page []
  (layout/render "home/ajax-example.html"))

(defn ajax-initial-data []
  (response {:ok "fooo" :loaded true}))

(defroutes home-routes
           (GET "/contact" [] (contact-page))
           (GET "/tos" [] (tos-page))
           (GET "/cookies" [] (cookies-page))
           (GET "/" [] (home-page))
           (GET "/example" [] (example-page))
           (GET "/ajax/page" [] (ajax-page))
           (GET "/ajax/page/init" [] (ajax-initial-data)))
