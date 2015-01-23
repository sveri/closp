(ns de.sveri.closp.routes.user
  (:require [compojure.core :refer :all]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [de.sveri.closp.layout :as layout]))

(defn admin-page [request]
  (if-not (authenticated? request)
    (throw-unauthorized)
    (layout/render "user/admin.html")))


(defroutes user-routes
           (GET "/user/admin" req (admin-page req)))
