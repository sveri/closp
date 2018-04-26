(ns {{ns}}.user.helper
  (:require [{{ns}}.localstorage :as ls]))

(defn get-jwt-token-from-localstorage []
  (ls/get-item ls/local-storage-jwt-token-key))


(defn user-is-logged-in? [db-or-user]
  (if-let [user (:user db-or-user)]
    (some? (:email user))
    (some? (:email db-or-user))))

(defn get-displayname [user]
  (:email user))
