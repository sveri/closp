(ns {{ns}}.service.auth
  (:require [buddy.auth :refer [authenticated?]]))

(defn unauthorized-access [_] true)
(defn rest-loggedin-access [req] (authenticated? req))

(def rest-rules
  [{:uris ["/api/user/signup" "/api/user/login"]
    :handler unauthorized-access}
   {:pattern #"^/api/.*"
    :handler rest-loggedin-access}])
