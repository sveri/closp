(ns {{ns}}.db.users
  (:require [korma.core :refer :all]
            [korma.db :refer [h2]])
  (:import (java.util UUID)))

(defentity users)

(defn get-all-users [ & [where-email-like]]
  (select users (where {:email [like (str "%" where-email-like "%")]})))

(defn get-user-by-email [email] (first (select users (where {:email email}) (limit 1))))
(defn get-user-by-act-id [id] (first (select users (where {:activationid id}) (limit 1))))
(defn get-user-by-uuid [uuid] (first (select users (where {:uuid uuid}) (limit 1))))

(defn username-exists? [email] (some? (get-user-by-email email)))

(defn create-user [email pw_crypted activationid & [is-active?]]
  (insert users (values {:email email :pass pw_crypted :activationid activationid :is_active (or is-active? false)
                         :uuid  (str (UUID/randomUUID))})))

(defn set-user-active [activationid & [active]]
  (update users (set-fields {:is_active (or active true)}) (where {:activationid activationid})))

(defn update-user [uuid fields]
  (update users (set-fields fields) (where {:uuid uuid})))

(defn change-password [email pw]
  (update users (set-fields {:pass pw}) (where {:email email})))
