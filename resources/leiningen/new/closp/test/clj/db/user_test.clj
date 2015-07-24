(ns {{ns}}.db.user-test
  (:require [clojure.test :refer :all]
    [korma.db :as k]
    [{{ns}}.db.user :as u]
    [joplin.core :as j]
    [joplin.jdbc.database]))

(def db-uri "jdbc:sqlite:./db/{{name}}-test.sqlite")
(def migrators "resources/migrators/sqlite")

(k/defdb temp-db db-uri)

; This fixture is intended to perform setup/teardown for each individual test in the namespace. Note that it assumes the :once fixture will handle creating/destroying the DB, while we only create/drop tables within the DB.
(defn db-setup [f]
  (do
    (j/rollback-db
      {:db {:type :sql,
            :url db-uri}
       :migrator migrators})
    (j/migrate-db
      {:db       {:type :sql,
                  :url  db-uri}
       :migrator migrators}))
  (f))

; Here we register another-fixture to wrap each test in the namespace
(use-fixtures :each db-setup)

(deftest get-all-users
  (u/create-user "email" "pw" "id")
  (let [users (u/get-all-users)]
    (is (= 2 (count users)))))