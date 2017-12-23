(ns {{ns}}.web.signup
  (:require [clojure.test :refer :all]
            [etaoin.api :refer :all]
            [etaoin.keys :as k]
            [{{ns}}.setup :as s]))

(use-fixtures :each s/browser-setup)
(use-fixtures :once s/server-setup)

(defn go-to-signup []
  (doto s/*driver*
    (go (str s/test-base-url "user/signup"))
    (wait-has-text {:tag :body} (s/t [:user/register]))))


(deftest ^:frontend wrong_email
  (go-to-signup)
  (doto s/*driver*
    (fill-multi [:email "foo"])
    (fill :email k/enter)
    (wait-has-text {:tag :body} (s/t [:user/email_invalid]))))

(deftest ^:frontend username_exists
  (go-to-signup)
  (doto s/*driver*
    (fill-multi [:email "admin@localhost.de"])
    (fill :email k/enter)
    (wait-has-text {:tag :body} (s/t [:user/username_exists]))))

(deftest ^:frontend passwords_dont_match
  (go-to-signup)
  (doto s/*driver*
    (fill-multi [:email "foo@bar.de"
                 :password "123456"
                 :confirm "2345677"])
    (fill :email k/enter)
    (wait-has-text {:tag :body} (s/t [:user/pass_match]))))

(deftest ^:frontend passwords_min_length
  (go-to-signup)
  (doto s/*driver*
    (fill-multi [:email "foo@bar.de"
                 :password "156"
                 :confirm "23"])
    (fill :email k/enter)
    (wait-has-text {:tag :body} (s/t [:user/pass_min_length]))))
