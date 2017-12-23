(ns {{ns}}.web.admin
  (:require [clojure.test :refer :all]
            [etaoin.api :refer :all]
            [etaoin.keys :as k]
            [{{ns}}.setup :as s]))

(use-fixtures :each s/browser-setup)
(use-fixtures :once s/server-setup)

(def password "bbbbbb")

(defn create-user [name]
  (doto s/*driver*
    (fill-multi [:email name
                 :password password
                 :confirm password])
    (fill :email k/enter)
    (wait-visible :flash-message)))

(defn sign-in [& [name pw link]]
  (doto s/*driver*
    (go (str s/test-base-url (or link "admin/users")))
    (fill-multi [:upper_email (or name "admin@localhost.de")
                 :upper_password (or pw "admin")])
    (fill :upper_password k/enter))
  (when (not link) (wait-visible s/*driver* :confirm)))

(deftest ^:frontend add-user
  (sign-in)
  (create-user "foo@bar.de")
  (wait-has-text s/*driver* :flash-message (s/t [:user/user_added]))
  (is (has-class? s/*driver* :flash-message "alert-success")))


(deftest ^:frontend add-user-invalid-mail
  (sign-in)
  (doto s/*driver*
    (fill-multi [:email "foo"
                 :password "bbbbbb"
                 :confirm "bbbbbb"])
    (fill :email k/enter)
    (wait-has-text {:tag :body} (s/t [:user/email_invalid]))))

(deftest ^:frontend pass-min-length
  (sign-in)
  (doto s/*driver*
    (fill-multi [:email "foo"
                 :password "bb"
                 :confirm "bbbb"])
    (fill :email k/enter)
    (wait-has-text {:tag :body} (s/t [:user/pass_min_length]))))

(deftest ^:frontend pass-dont-match
  (sign-in)
  (doto s/*driver*
    (fill-multi [:email "foo"
                 :password "bbwerwerwer"
                 :confirm "bbbbsdfdf"])
    (fill :email k/enter)
    (wait-has-text {:tag :body} (s/t [:user/pass_match]))))

(deftest ^:frontend cancel-delete-user
  (sign-in)
  (let [uname "_foo@bar.de"]
    (create-user uname)
    (doto s/*driver*
      (click {:css "input.btn.btn-danger"})
      (click {:css "input.btn.btn-primary"})
      (wait-has-text {:tag :body} (s/t [:generic/deletion_canceled])))
    (is (has-text? s/*driver* {:tag :body} uname))))

(deftest ^:frontend delete-user
  (sign-in)
  (let [uname "_aadmin@bar.de"]
    (create-user uname)
    (doto s/*driver*
      (click {:css "input.btn.btn-danger"})
      (click {:css "input.btn.btn-danger"})
      (wait-has-text {:tag :body} (s/t [:user/deleted])))
    (is (not (has-text? s/*driver* {:tag :body} uname)))))

(deftest ^:frontend set-active->logout->change_password
  (sign-in)
  (let [uname "aaoo@bar.de"]
    (create-user uname)
    (doto s/*driver*
      (click {:tag :input :type "checkbox"})
      (click {:tag :input :type "submit" :value (s/t [:admin/update])})
      (wait-has-text {:tag :body} (s/t [:user/updated] [uname]))
      (go (str s/test-base-url "user/logout")))
    (sign-in uname password "user/changepassword")
    (doto s/*driver*
      (wait-has-text {:tag :body} (s/t [:user/change_password]))
      (fill-multi [:oldpassword password
                   :password "dddddd"
                   :confirm "dddddd"])
      (fill :confirm k/enter)
      (wait-has-text {:tag :body} (s/t [:user/pass_changed]))
      (go (str s/test-base-url "user/logout")))
    (sign-in uname "dddddd" "user/changepassword")
    (wait-has-text s/*driver* {:tag :body} (s/t [:user/change_password]))
    (is (has-text? s/*driver* {:tag :body} uname))))
