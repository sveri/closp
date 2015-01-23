(ns {{ns}}.auth
  (:require [buddy.auth.backends.session :refer [session-backend]]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [ring.util.response :refer [redirect]]
    [{{ns}}.layout :as layout]))

(defn unauthorized-handler
  [request _]
  (cond
    (authenticated? request)
    (layout/render "app.html" {:status 403})
    :else
    (let [current-url (:uri request)]
      (redirect (format "/?next=%s" current-url)))))

(def auth-backend
  (session-backend {:unauthorized-handler unauthorized-handler}))
