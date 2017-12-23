(ns {{ns}}.web.user
  (:require [clojure.test :refer :all]
            [etaoin.api :refer :all]
            [etaoin.keys :as k]
            [{{ns}}.setup :as s]))

(use-fixtures :each s/browser-setup)
(use-fixtures :once s/server-setup)

(deftest ^:frontend login-invalid-username
  (doto s/*driver*
    (go (str s/test-base-url))
    (fill-multi [:upper_email "foo"])
    (fill :upper_password k/enter)
    (wait-has-text {:tag :body} (s/t [:user/username_wrong]))))

(deftest ^:frontend login-invalid-password
  (doto s/*driver*
    (go (str s/test-base-url))
    (fill-multi [:upper_email "admin@localhost.de"
                 :upper_password "uiatern"])
    (fill :upper_password k/enter)
    (wait-has-text {:tag :body} (s/t [:user/pass_correct]))))

(deftest ^:frontend login-and-forward
  (doto s/*driver*
    (go (str s/test-base-url))
    (fill-multi [:upper_email "admin@localhost.de"
                 :upper_password "admin"])
    (fill :upper_password k/enter)
    (wait-has-text {:tag :body} (s/t [:user/users]))))