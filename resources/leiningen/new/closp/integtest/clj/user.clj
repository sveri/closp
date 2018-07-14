(ns {{ns}}.user
  (:require [clojure.test :refer :all]
            [clj-http.client :as c]
            [clojure.data.json :as j]
            [{{ns}}.setup :as s]
            [{{ns}}.db.user :as db-u]))

(use-fixtures :once s/server-setup)

(use-fixtures :each s/db-setup)


(defn post-with-body [url body & [auth]]
  (c/post (str s/test-base-url url)
          (merge
            (when auth {:headers {"authorization" (str "Token " auth)}})
            {:body             (j/write-str body)
             :content-type     :json
             :accept           :json
             :throw-exceptions false})))

(deftest ^:rest invalid-signup
  (is (= 500 (:status (post-with-body "api/user/signup" {:email "" :password ""}))))
  (is (= 500 (:status (post-with-body "api/user/signup" {:email "a@a.de" :password ""}))))
  (is (= 500 (:status (post-with-body "api/user/signup" {:email "a@a.de" :password "paa"})))))

(deftest ^:rest username-exists
  (is (= 200 (:status (post-with-body "api/user/signup" {:email "a@a.de" :password "password"}))))
  (is (= 500 (:status (post-with-body "api/user/signup" {:email "a@a.de" :password "password"}))))
  (is (.contains (:body (post-with-body "api/user/signup" {:email "a@a.de" :password "password"})) "exists")))

(deftest ^:rest username-can-signup
  (is (= 200 (:status (post-with-body "api/user/signup" {:email "a@a.de" :password "password"}))))
  (is (not (nil? (db-u/get-user-by-email s/db "a@a.de")))))

(deftest ^:rest username-can-login
  (is (= 200 (:status (post-with-body "api/user/signup" {:email "a@a.de" :password "password"}))))
  (is (not (nil? (get (j/read-str (:body (post-with-body "api/user/login" {:email "a@a.de" :password "password"}))) "token")))))

(deftest ^:rest username-cannot-login
  (is (= 200 (:status (post-with-body "api/user/signup" {:email "a@a.de" :password "password"}))))
  (is (= 401 (:status (post-with-body "api/user/login" {:email "a@a.de" :password "passwor"}))))
  (is (= 401 (:status (post-with-body "api/user/login" {:email "a@a.e" :password "password"})))))

(deftest ^:rest cannot-change-password-without-token
  (is (= 200 (:status (post-with-body "api/user/signup" {:email "a@a.de" :password "password"}))))
  (is (= 403 (:status (post-with-body "api/user/changepassword" {:current-password "password" :password "passwor"})))))

(deftest ^:rest can-change-password
  (let [token (get (j/read-str (:body (post-with-body "api/user/signup" {:email "f@a.de" :password "password"}))) "token")]
    (is (= 200 (:status (post-with-body "api/user/changepassword" {:current-password "password" :password "new-pass"} token))))))

