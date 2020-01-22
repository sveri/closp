(ns {{namespace}}.frontend.user.admin-test
  (:require [clojure.test :refer :all]
            [etaoin.api :refer :all]
            [etaoin.keys :as k]
            [{{namespace}}.frontend.setup :as s]))

(use-fixtures :each s/browser-setup)
(use-fixtures :once s/server-setup)

(def password "bbbbbb")

(defn create-user [name]
  (doto s/*driver*
    (fill-multi [:displayname "some-displayname"
                 :email name
                 :password password])
    (fill :email k/enter)
    (wait 1) ;wait one second to make sure database is ready
    (wait-has-text {:tag :body} (s/t [:admin/add_user]))))


(defn sign-in [& [name pw link]]
  (doto s/*driver*
    (go (str s/test-base-url (or link "admin/users")))
    (fill-multi [:email (or name "admin@localhost.de")
                 :password (or pw "admin")])
    (fill :password k/enter))
  (when (not link) (wait-visible s/*driver* :displayname)))

(defn wait-and-is [text]
  (wait-has-text s/*driver* {:tag :body} text)
  (is (has-text? s/*driver* {:tag :body} text)))

(deftest ^:frontend add-user
  (sign-in)
  (create-user "foo@bar.de")
  (wait-and-is "foo@bar.de"))

(deftest ^:frontend cancel-delete-user
  (sign-in)
  (let [uname "_foo@bar.de"]
    (create-user uname)
    (doto s/*driver*
      (click {:css "button.btn.waves-effect.waves-light.red.lighten-3"})
      (wait-visible {:id "really-delete-cancel"})
      (click {:id "really-delete-cancel"}))
    (wait-and-is uname)))

(deftest ^:frontend delete-user
  (sign-in)
  (let [uname "_aadmin@bar.de"]
    (create-user uname)
    (doto s/*driver*
      (click {:css "button.btn.waves-effect.waves-light.red.lighten-3"})
      (click {:id "really-delete-delete"})
      (wait 1)
      (wait-has-text {:tag :body} (s/t [:admin/add_user])))
    (is (has-text? s/*driver* {:tag :body} (s/t [:user/deleted] [uname])))))

(deftest ^:frontend set-new-user-inactive->logout->should-not-be-able-to-login
  (sign-in)
  (let [uname "aaoo@bar.de"]
    (create-user uname)
    (doto s/*driver*
      (click {:tag :span :class "for-active-checkbox"})
      (click {:tag :button :type "submit" :value (s/t [:admin/update])})
      (wait 1)
      (wait-has-text {:tag :body} (s/t [:user/updated] [uname]))
      (go (str s/test-base-url "user/logout"))
      (wait 5))
    (sign-in uname password "user/changepassword"))
  (wait-and-is (s/t [:user/inactive])))
