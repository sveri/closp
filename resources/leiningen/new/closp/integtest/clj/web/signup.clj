(ns {{ns}}.web.signup
  (:require [clojure.test :refer :all]
            [reloaded.repl :refer [go stop]]
            [{{ns}}.components.components :refer [prod-system]]
            [clj-webdriver.taxi :refer :all]))


(def test-base-url (str "http://localhost:3000/"))

(defn start-browser []
  (set-driver! {:browser :htmlunit}))

(defn stop-browser []
  (quit))

(defn start-server []
  (reloaded.repl/set-init! prod-system)
  (go))

(defn stop-server []
  (stop))

(deftest ^:integration homepage-greeting
  (start-server)
  (start-browser)
  (to test-base-url)
  (is (.contains (text "body") "Foo!"))
  (stop-browser)
  (stop-server))
