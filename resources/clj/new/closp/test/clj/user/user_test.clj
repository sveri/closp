(ns {{namespace}}.frontend.user.user-test
  (:require [clojure.test :refer :all]
            [etaoin.api :refer :all]
            [etaoin.keys :as k]
            [{{namespace}}.frontend.setup :as s]))

(use-fixtures :each s/browser-setup)
(use-fixtures :once s/server-setup)

(deftest ^:frontend login-invalid-username
  (doto s/*driver*
    (go (str s/test-base-url "user/login"))
    (fill-multi [:email "foo@bar.d" :password "foobarfoo"])
    (fill :password k/enter)
    (wait-has-text {:tag :body} (s/t [:user/email_wrong])))
  (is (has-text? s/*driver* (s/t [:user/email_wrong]))))

(deftest ^:frontend login-invalid-password
  (doto s/*driver*
    (go (str s/test-base-url "user/login"))
    (fill-multi [:email "admin@localhost.de" :password "uiatern"])
    (fill :password k/enter)
    (wait-has-text {:tag :body} (s/t [:user/pass_correct])))
  (is (has-text? s/*driver* (s/t [:user/pass_correct]))))

(deftest ^:frontend change-password-success
  (doto s/*driver*
    (go (str s/test-base-url "user/changepassword"))
    (wait-has-text {:tag :body} (s/t [:generic/password]))
    (fill-multi [:email "admin@localhost.de" :password "admin"])
    (fill :password k/enter)
    (wait-has-text {:tag :body} (s/t [:user/current_password]))
    (fill-multi [:oldpassword "admin" :password "adminnew" :confirm "adminnew"])
    (fill :password k/enter)
    (wait-has-text {:tag :body} (s/t [:user/pass_changed])))
  (is (has-text? s/*driver* (s/t [:user/pass_changed]))))

(deftest ^:frontend change-password-dont-match
  (doto s/*driver*
    (go (str s/test-base-url "user/changepassword"))
    (wait-has-text {:tag :body} (s/t [:generic/password]))
    (fill-multi [:email "admin@localhost.de" :password "admin"])
    (fill :password k/enter)
    (wait-has-text {:tag :body} (s/t [:user/current_password]))
    (fill-multi [:oldpassword "admin" :password "adminnew" :confirm "adminne"])
    (fill :password k/enter)
    (wait-has-text {:tag :body} (s/t [:user/pass_match])))
  (is (has-text? s/*driver* (s/t [:user/pass_match]))))

(deftest ^:frontend change-password-old-is-wrong
  (doto s/*driver*
    (go (str s/test-base-url "user/changepassword"))
    (wait-has-text {:tag :body} (s/t [:generic/password]))
    (fill-multi [:email "admin@localhost.de" :password "admin"])
    (fill :password k/enter)
    (wait-has-text {:tag :body} (s/t [:user/current_password]))
    (fill-multi [:oldpassword "admilkdfj" :password "adminnew" :confirm "adminnew"])
    (fill :password k/enter)
    (wait-has-text {:tag :body} (s/t [:user/wrong_cur_pass])))
  (is (has-text? s/*driver* (s/t [:user/wrong_cur_pass]))))

;(deftest ^:frontend login-and-forward
;  (doto s/*driver*
;    (go (str s/test-base-url))
;    (fill-multi [:upper_email "admin@localhost.de"
;                 :upper_password "admin"])
;    (fill :upper_password k/enter)
;    (wait-has-text {:tag :body} (s/t [:user/users]))))