(ns {{ns}}.db.user-test
  (:require [clojure.test :refer :all]
            [clojure.java.jdbc :as j]
            [{{ns}}.db.user :as u]))

(def db {:connection-uri "jdbc:postgresql://localhost:5432/{{name}}-test?user={{name}}&password={{name}}"})

; This fixture is intended to perform setup/teardown for each individual test in the namespace.
; Note that it assumes the :once fixture will handle creating/destroying the DB,
; while we only create/drop tables within the DB.
(defn db-setup [f]
  (j/execute! db ["truncate table users cascade"])
  (f))

;; Here we register another-fixture to wrap each test in the namespace
(use-fixtures :each db-setup)

(deftest get-all-users
  (u/create-user db "email" "pw")
  (let [users (u/get-all-users db)]
    (is (= 1 (count users)))))

