(ns {{namespace}}.service.user-service)

(defn get-loggedin-email-address [req]
  (get-in req [:session :user :email]))
