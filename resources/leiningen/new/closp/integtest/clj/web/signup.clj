(ns {{ns}}.web.signup
  (:require [clojure.test :refer :all]
            [clj-webdriver.taxi :refer :all]
            [{{ns}}.web.setup :as s]
            [{{ns}}.db.user :as db]))

(use-fixtures :each s/browser-setup)
(use-fixtures :once s/server-setup)

(defn signup-valid-user []
  (to (str s/test-base-url "user/signup"))
  (quick-fill-submit {"#email" "foo@bar.de"}
                     {"#password" "bbbbbb"}
                     {"#confirm" "bbbbbb"}
                     {"#email" submit}))

(deftest ^:integration homepage-greeting
  (to s/test-base-url)
  (is (.contains (text "body") "Foo!")))

(deftest ^:integration wrong_email
  (to (str s/test-base-url "user/signup"))
  (quick-fill-submit {"#email" "foo"}
                     {"#email" submit})
  (is (.contains (text "body") (s/t :en :user/email_invalid))))

(deftest ^:integration username_exists
  (to (str s/test-base-url "user/signup"))
  (quick-fill-submit {"#email" "admin@localhost.de"}
                     {"#email" submit})
  (is (.contains (text "body") (s/t :en :user/username_exists))))

(deftest ^:integration passwords_dont_match
  (to (str s/test-base-url "user/signup"))
  (quick-fill-submit {"#email" "foo@bar.de"}
                     {"#password" "123456"}
                     {"#configrm" "23456"}
                     {"#email" submit})
  (is (.contains (text "body") (s/t :en :user/pass_match))))

(deftest ^:integration passwords_min_length
  (to (str s/test-base-url "user/signup"))
  (quick-fill-submit {"#email" "foo@bar.de"}
                     {"#password" "156"}
                     {"#confirm" "23"}
                     {"#email" submit})
  (is (.contains (text "body") (s/t :en :user/pass_min_length))))

(deftest ^:integration account_created
  (signup-valid-user)
  (is (.contains (text "body  ") (s/t :en :user/account_created))))

(deftest ^:integration account_validated
  (signup-valid-user)
  (->> (db/get-user-by-email "foo@bar.de")
       :activationid
       (str s/test-base-url "user/activate/")
       to)
  (is (.contains (text "body  ") (s/t :en :user/account_activated))))
