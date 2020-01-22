(ns {{namespace}}.frontend.user.signup-test
  (:require [clojure.test :refer :all]
            [etaoin.api :refer :all]
            [etaoin.keys :as k]
            [{{namespace}}.frontend.setup :as s]))

(use-fixtures :each s/browser-setup)
(use-fixtures :once s/server-setup)

(defn go-to-signup []
  (doto s/*driver*
    (go (str s/test-base-url "user/signup"))
    (wait-has-text {:tag :body} (s/t [:user/register]))))

(deftest ^:frontend username_exists
  (go-to-signup)
  (doto s/*driver*
    (fill-multi [:displayname "admin2" :email "admin@localhost.de" :password "sdfkljsdf"])
    (fill :email k/enter)
    (wait-has-text {:tag :body} (s/t [:user/email_exists])))
  (is (has-text? s/*driver* (s/t [:user/email_exists]))))

(deftest ^:frontend signup-worked
  (go-to-signup)
  (doto s/*driver*
    (fill-multi [:displayname "admin2" :email "admin2@localhost.de" :password "sdfkljsdf"])
    (fill :email k/enter)
    (wait-has-text {:tag :body} "Closp Template"))
  (is (has-text? s/*driver* "Closp Template")))
