(ns {{ns}}.db.user-test
  (:require [clojure.test :refer :all]
    [korma.db :as k]
    [joplin.alias :as a]
    [joplin.repl :as r]
    [joplin.jdbc.database]
    [{{ns}}.db.user :as u]))

(def db-uri "jdbc:sqlite:./db/{{name}}-test.sqlite")
(def jop-config (a/*load-config* "joplin.edn"))

; This fixture is intended to perform setup/teardown for each individual test in the namespace.
; Note that it assumes the :once fixture will handle creating/destroying the DB,
; while we only create/drop tables within the DB.
(defn db-setup [f]
  (do
    (k/defdb temp-db db-uri)
    (r/reset jop-config :sqlite-test-env :sqlite-test))
  (f))

; Here we register another-fixture to wrap each test in the namespace
(use-fixtures :each db-setup)

(deftest get-all-users
  (u/create-user "email" "pw" "id")
  (let [users (u/get-all-users)]
    (is (= 2 (count users)))))