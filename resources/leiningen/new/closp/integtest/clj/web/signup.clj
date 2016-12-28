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

(deftest ^:selenium wrong_email
  (to (str s/test-base-url "user/signup"))
  (quick-fill-submit {"#email" "foo"}
                     {"#email" submit})
  (is (.contains (text "body") (s/t [:user/email_invalid]))))

(deftest ^:selenium username_exists
  (to (str s/test-base-url "user/signup"))
  (quick-fill-submit {"#email" "admin@localhost.de"}
                     {"#email" submit})
  (is (.contains (text "body") (s/t [:user/username_exists]))))

(deftest ^:selenium passwords_dont_match
  (to (str s/test-base-url "user/signup"))
  (quick-fill-submit {"#email" "foo@bar.de"}
                     {"#password" "123456"}
                     {"#configrm" "23456"}
                     {"#email" submit})
  (is (.contains (text "body") (s/t [:user/pass_match]))))

(deftest ^:selenium passwords_min_length
  (to (str s/test-base-url "user/signup"))
  (quick-fill-submit {"#email" "foo@bar.de"}
                     {"#password" "156"}
                     {"#confirm" "23"}
                     {"#email" submit})
  (is (.contains (text "body") (s/t [:user/pass_min_length]))))
