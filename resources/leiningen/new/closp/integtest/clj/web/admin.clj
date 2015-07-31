(ns {{ns}}.web.admin
  (:require [clojure.test :refer :all]
            [clj-webdriver.taxi :refer :all]
            [foo.bar.web.setup :as s]))

(use-fixtures :each s/browser-setup)
(use-fixtures :once s/server-setup)

(defn sign-in->admin-view []
  (to (str s/test-base-url "admin/users"))
  (quick-fill-submit {"#upper_email" "admin@localhost.de"}
                     {"#upper_password" "admin"}
                     {"#upper_password" submit}))

(deftest ^:integration add-user
  (sign-in->admin-view)
  (quick-fill-submit {"#email" "foo@bar.de"}
                     {"#password" "bbbbbb"}
                     {"#confirm" "bbbbbb"}
                     {"#email" submit})
  (is (.contains (text "body") (s/t :en :user/user_added)))
  (is (find-element {:css "div#flash-message.alert-success"})))

(deftest ^:integration add-user-invalid-mail
  (sign-in->admin-view)
  (quick-fill-submit {"#email" "foo"}
                     {"#password" "bbbbbb"}
                     {"#confirm" "bbbbbb"}
                     {"#email" submit})
  (is (.contains (text "body") (s/t :en :user/email_invalid))))

(deftest ^:integration pass-min-length
  (sign-in->admin-view)
  (quick-fill-submit {"#email" "foo"}
                     {"#password" "bb"}
                     {"#confirm" "b"}
                     {"#email" submit})
  (is (.contains (text "body") (s/t :en :user/pass_min_length))))

(deftest ^:integration pass-dont-match
  (sign-in->admin-view)
  (quick-fill-submit {"#email" "foo"}
                     {"#password" "bbuaeuiae"}
                     {"#confirm" "bcxvlcvxlc"}
                     {"#email" submit})
  (is (.contains (text "body") (s/t :en :user/pass_match))))
