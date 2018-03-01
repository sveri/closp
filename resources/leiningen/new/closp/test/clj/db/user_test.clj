(ns {{ns}}.db.user-test
  (:require [clojure.test :refer :all]
            [clojure.java.jdbc :as j]
            [{{ns}}.db.user :as u]))

(def db {:connection-uri "jdbc:postgresql://localhost:5432/{{name}}_test?user={{name}}&password={{name}}"})

; This fixture is intended to perform setup/teardown for each individual test in the namespace.
; Note that it assumes the :once fixture will handle creating/destroying the DB,
; while we only create/drop tables within the DB.
(defn db-setup [f]
  (j/execute! db ["drop table if exists users;"])
  (j/execute! db ["CREATE TABLE users ( id bigserial NOT NULL PRIMARY KEY, first_name character varying(30), last_name character varying(30), role character varying(30), email character varying(30) NOT NULL, last_login time, is_active BOOLEAN DEFAULT FALSE NOT NULL, pass character varying(200));"])
  (f))

;; Here we register another-fixture to wrap each test in the namespace
(use-fixtures :each db-setup)

(deftest get-all-users
  (u/create-user db "email" "pw")
  (let [users (u/get-all-users db)]
    (is (= 1 (count users)))))

