(ns {{ns}}.db.core
  (:require [korma.core :refer :all]
            [korma.db :refer [defdb h2]]))

(def db-spec {:connection-uri "jdbc:h2:./db/korma.db"})

(defdb db db-spec)

(defentity users)

(defn get-all-users []
  (select users))

(defn get-user-by-email [email] (first (select users (where {:email email}) (limit 1))))
(defn get-user-by-act-id [id] (first (select users (where {:activationid id}) (limit 1))))

(defn username-exists? [email] (= 1 (count (get-user-by-email email))))

(defn create-user [email pw_crypted activationid & [is-active?]]
  (insert users (values {:email email :pass pw_crypted :activationid activationid :is_active (or is-active? false)})))

(defn set-user-active [activationid & [active]]
  (update users (set-fields {:is_active (or active true)}) (where {:activationid activationid})))

;(defn is-active? [email]
;  (get (get-user-by-email email) :is_active false))

(defn change-password [email pw]
  (update users (set-fields {:pass pw}) (where {:email email})))
