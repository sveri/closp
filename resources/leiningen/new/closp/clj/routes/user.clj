(ns {{ns}}.user
  (:require [compojure.core :refer :all]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [{{ns}}.layout :as layout]))

(defn admin-page [request]
  (if-not (authenticated? request)
    (throw-unauthorized)
    (layout/render "user/admin.html")))

(defn login-page []
  (layout/render "user/login.html"))


(defroutes user-routes
           (GET "/user/admin" req (admin-page req))
           (GET "/user/login" [] (login-page)))
