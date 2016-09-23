(ns {{ns}}.db.user
  (:require [korma.core :refer [select where insert delete values update set-fields defentity limit order]]
            [korma.db :refer [h2]]
            [{{ns}}.db.entities :refer [user]]))

(defn get-all-users [ & [where-email-like]]
  (select user (where {:email [like (str "%" where-email-like "%")]})
          (order :email :asc)))

(defn get-user-by-email [email] (first (select user (where {:email email}) (limit 1))))
(defn get-user-by-id [id] (first (select user (where {:id id}) (limit 1))))

(defn username-exists? [email] (some? (get-user-by-email email)))

(defn create-user [email pw_crypted]
  (insert users (values {:email email :pass pw_crypted :is_active true})))

(defn update-user [id fields] (update user (set-fields fields) (where {:id id})))
(defn delete-user [id] (delete user (where {:id id})))
(defn change-password [email pw] (update user (set-fields {:pass pw}) (where {:email email})))
