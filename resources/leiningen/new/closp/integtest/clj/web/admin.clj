(ns {{ns}}.web.admin
  (:require [clojure.test :refer :all]
            [clj-webdriver.taxi :refer :all]
            [{{ns}}.web.setup :as s]))

(use-fixtures :each s/browser-setup)
(use-fixtures :once s/server-setup)

(defn ->user [name]
  (quick-fill-submit {"#email" name}
                     {"#password" "bbbbbb"}
                     {"#confirm" "bbbbbb"}
                     {"#email" submit}))

(defn sign-in [& [name pw link]]
  (to (str s/test-base-url (or link "admin/users")))
  (quick-fill-submit {"#upper_email" (or name"admin@localhost.de")}
                     {"#upper_password" (or pw "admin")}
                     {"#upper_password" submit}))

(deftest ^:integration add-user
  (sign-in)
  (->user "foo@bar.de")
  (is (.contains (text "body") (s/t :en :user/user_added)))
  (is (find-element {:css "div#flash-message.alert-success"})))

(deftest ^:integration add-user-invalid-mail
  (sign-in)
  (quick-fill-submit {"#email" "foo"}
                     {"#password" "bbbbbb"}
                     {"#confirm" "bbbbbb"}
                     {"#email" submit})
  (is (.contains (text "body") (s/t :en :user/email_invalid))))

(deftest ^:integration pass-min-length
  (sign-in)
  (quick-fill-submit {"#email" "foo"}
                     {"#password" "bb"}
                     {"#confirm" "b"}
                     {"#email" submit})
  (is (.contains (text "body") (s/t :en :user/pass_min_length))))

(deftest ^:integration pass-dont-match
  (sign-in)
  (quick-fill-submit {"#email" "foo"}
                     {"#password" "bbuaeuiae"}
                     {"#confirm" "bcxvlcvxlc"}
                     {"#email" submit})
  (is (.contains (text "body") (s/t :en :user/pass_match))))

(deftest ^:integration cancel-delete-user
  (sign-in)
  (let [uname "_foo@bar.de"]
    (->user uname)
    (click (find-element {:css "input.btn.btn-danger"}))
    (click (find-element {:css "input.btn.btn-primary"}))
    (is (.contains (text "body") uname))
    (is (.contains (text "body") (s/t :en :generic/deletion_canceled)))))

(deftest ^:integration delete-user
  (sign-in)
  (let [uname "_aadmin@bar.de"]
    (->user uname)
    (click (find-element {:css "input.btn.btn-danger"}))
    (click (find-element {:css "input.btn.btn-danger"}))
    (is (not (.contains (text "body") uname)))
    (is (.contains (text "body") (s/t :en :user/deleted)))))

(deftest ^:integration set-active->logout->change_password
  (sign-in)
  (let [uname "_foo@bar.de"]
    (->user uname)
    (click (find-element {:tag :input :type "checkbox"}))
    (click (find-element {:tag :input :type "submit" :value "Update"}))
    (to (str s/test-base-url "user/logout"))
    (sign-in uname "bbbbbb" "user/changepassword")
    (quick-fill-submit {"#oldpassword" "bbbbbb"}
                       {"#password" "dddddd"}
                       {"#confirm" "dddddd"}
                       {"#confirm" submit})
    (is (.contains (text "body") (s/t :en :user/pass_changed)))
    (sign-in uname "dddddd" "user/changepassword")
    (is (.contains (text "body") uname))))