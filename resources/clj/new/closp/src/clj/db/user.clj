(ns {{namespace}}.db.user
  (:require [clojure.java.jdbc :as j]))

(defn get-all-users [db & [where-email-like]]
  (let [search-param (str "%" (or where-email-like "") "%")]
    (j/query db ["select * from users where email like ? or displayname like ?
                 order by email asc" search-param search-param])))

(defn get-user-by-email [db email]
  (first (j/query db ["select * from users where email = ? limit 1" email])))

(defn get-user-by-id [db id]
  (first (j/query db ["select * from users where id = ? limit 1" id])))

(defn email-exists? [db email] (some? (get-user-by-email db email)))

(defn create-user [db email displayname pw_crypted]
  (j/insert! db :users {:email email :password pw_crypted :displayname displayname :is_active true :role "user"}))

(defn update-user [db user-email fields]
  (j/update! db :users fields ["email = ?" user-email]))

(defn delete-user [db email] (j/delete! db :users ["email = ?" email]))

(defn change-password [db email pw] (j/update! db :users {:password pw} ["email = ?" email]))

