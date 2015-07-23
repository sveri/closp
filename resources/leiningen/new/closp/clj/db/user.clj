(ns {{ns}}.db.user
  (:require [korma.core :refer [select where insert delete values update set-fields defentity limit]]
            [korma.db :refer [h2]])
  (:import (java.util UUID)))

(defentity user)

(defn get-all-users [ & [where-email-like]]
  (select user (where {:email [like (str "%" where-email-like "%")]})))

(defn get-user-by-email [email] (first (select user (where {:email email}) (limit 1))))
(defn get-user-by-act-id [id] (first (select user (where {:activationid id}) (limit 1))))
(defn get-user-by-uuid [uuid] (first (select user (where {:uuid uuid}) (limit 1))))

(defn username-exists? [email] (some? (get-user-by-email email)))

(defn create-user [email pw_crypted activationid & [is-active?]]
  (insert user (values {:email email :pass pw_crypted :activationid activationid :is_active (or is-active? false)
                         :uuid  (str (UUID/randomUUID))})))

(defn set-user-active [activationid & [active]]
  (update user (set-fields {:is_active (or active true)}) (where {:activationid activationid})))

(defn update-user [uuid fields] (update user (set-fields fields) (where {:uuid uuid})))
(defn delete-user [uuid] (delete user (where {:uuid uuid})))
(defn change-password [email pw] (update user (set-fields {:pass pw}) (where {:email email})))
