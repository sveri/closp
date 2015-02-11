(ns {{ns}}.service.auth
  (:require [buddy.auth.backends.session :refer [session-backend]]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [ring.util.response :refer [redirect]]
            [noir.session :as sess]
            [{{ns}}.layout :as layout]))

(def ^:const available-roles ["admin" "none"])

(defn admin-access [_] (= "admin" (sess/get :role)))
(defn unauthorized-access [_] true)
(defn loggedin-access [_] (some? (sess/get :identity)))

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
    (redirect (format "/user/login?next=%s" current-url))))

(def auth-backend
  (session-backend {:unauthorized-handler unauthorized-handler}))
