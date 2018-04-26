(ns {{ns}}.routes.api
  (:require [compojure.core :refer [routes GET POST]]
            [ring.util.http-response :as resp]
            [{{ns}}.db.user :as db-u]
            [{{ns}}.service.user :as s-u]))

(defn get-initial-data [db req]
  (resp/ok {:user (dissoc (db-u/get-user-by-email db (s-u/get-user-id-from-req req)) :pass)}))

(defn api-routes [db]
  (routes
    (GET "/api/data/initial" req (get-initial-data db req))))
