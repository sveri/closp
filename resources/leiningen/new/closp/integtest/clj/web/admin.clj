(ns {{ns}}.web.admin
  (:require [clojure.test :refer :all]
            [clj-webdriver.taxi :refer :all]
            [{{ns}}.setup :as s]))

(use-fixtures :each s/browser-setup)
(use-fixtures :each s/browser-setup)
(use-fixtures :once s/server-setup)

(defn ->user [name]
  (fill-multi s/driver
              [{:tag :input :id :email} name]))
;(quick-fill-submit {"#email" name}
;                   {"#password" "bbbbbb"}
;                   {"#confirm" "bbbbbb"}
;                   {"#email" submit}))

(defn sign-in [& [name pw link]]
  (doto s/driver
    (go (str s/test-base-url (or link "admin/users")))
    ;(eta/fill s/driver :upper_email (or name "admin@localhost.de")))
    (fill-multi [:upper_email (or name "admin@localhost.de")
                 :upper_password (or pw "admin")])
    (fill :upper_password k/enter)))
;(quick-fill-submit {"#upper_email" (or
; name"admin@localhost.de")}
;                   {"#upper_password" (or pw "admin")}
;                   {"#upper_password" submit}))

(deftest ^:selenium add-user
  (sign-in)
  (wait s/driver 5))
;(->user "foo@bar.de")
;(is (.contains (text "body") (s/t [:user/user_added])))
;(is (find-element {:css "div#flash-message.alert-success"})))

(deftest ^:selenium add-user2
  (sign-in))

;(deftest ^:selenium add-user-invalid-mail
;  (sign-in)
;  (quick-fill-submit {"#email" "foo"}
;                     {"#password" "bbbbbb"}
;                     {"#confirm" "bbbbbb"}
;                     {"#email" submit})
;  (is (.contains (text "body") (s/t [:user/email_invalid]))))
;
;(deftest ^:selenium pass-min-length
;  (sign-in)
;  (quick-fill-submit {"#email" "foo"}
;                     {"#password" "bb"}
;                     {"#confirm" "b"}
;                     {"#email" submit})
;  (is (.contains (text "body") (s/t [:user/pass_min_length]))))
;
;(deftest ^:selenium pass-dont-match
;  (sign-in)
;  (quick-fill-submit {"#email" "foo"}
;                     {"#password" "bbuaeuiae"}
;                     {"#confirm" "bcxvlcvxlc"}
;                     {"#email" submit})
;  (is (.contains (text "body") (s/t [:user/pass_match]))))
;
;(deftest ^:selenium cancel-delete-user
;  (sign-in)
;  (let [uname "_foo@bar.de"]
;    (->user uname)
;    (click (find-element {:css "input.btn.btn-danger"}))
;    (click (find-element {:css "input.btn.btn-primary"}))
;    (is (.contains (text "body") uname))
;    (is (.contains (text "body") (s/t [:generic/deletion_canceled])))))
;
;(deftest ^:selenium delete-user
;  (sign-in)
;  (let [uname "_aadmin@bar.de"]
;    (->user uname)
;    (click (find-element {:css "input.btn.btn-danger"}))
;    (click (find-element {:css "input.btn.btn-danger"}))
;    (is (not (.contains (text "body") uname)))
;    (is (.contains (text "body") (s/t [:user/deleted])))))
;
;(deftest ^:selenium set-active->logout->change_password
;  (sign-in)
;  (let [uname "_foo@bar.de"]
;    (->user uname)
;    (click (find-element {:tag :input :type "checkbox"}))
;    (click (find-element {:tag :input :type "submit" :value (s/t [:admin/update])}))
;    (to (str s/test-base-url "user/logout"))
;    (sign-in uname "bbbbbb" "user/changepassword")
;    (quick-fill-submit {"#oldpassword" "bbbbbb"}
;                       {"#password" "dddddd"}
;                       {"#confirm" "dddddd"}
;                       {"#confirm" submit})
;    (is (.contains (text "body") (s/t [:user/pass_changed])))
;    (sign-in uname "dddddd" "user/changepassword")
;    (is (.contains (text "body") uname))))
