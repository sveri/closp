(ns {{namespace}}.service.auth
  (:require [buddy.auth.backends.session :refer [session-backend]]
            [ring.util.response :refer [redirect]]))

(def ^:const available-roles ["admin" "user"])

(defn admin-access [req] (= "admin" (-> req :session :user :role)))
(defn loggedin-access [req] (some? (-> req :session :user :email)))
(defn unauthorized-access [_] true)

(def rules [{:pattern #"^/admin.*"
             :handler admin-access}
            {:pattern #"^/user/changepassword"
             :handler loggedin-access}
            {:pattern #"^/user.*"
             :handler unauthorized-access}
            {:pattern #"^/"
             :handler unauthorized-access}])

(defn unauthorized-handler
  [request _]
  (let [current-url (:uri request)]
    (redirect (format "/user/login?nexturl=%s" current-url))))

(def auth-backend
  (session-backend {:unauthorized-handler unauthorized-handler}))
