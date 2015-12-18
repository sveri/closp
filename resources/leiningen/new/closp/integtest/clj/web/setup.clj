(ns {{ns}}.web.setup
  (:require [clj-webdriver.taxi :as w]
            [joplin.core :as j]
            [taoensso.tower :as tower]
            [{{ns}}.components.config]
            [{{ns}}.components.locale :refer [get-tconfig]]))

(def db-uri "jdbc:sqlite:./db/{{name}}-integ-test.sqlite")
(def migrators "resources/migrators/sqlite")

; custom config for configuration
(def test-config
  {:hostname                "http://localhost/"
   :mail-from               "info@localhost.de"
   :mail-type               :test
   :activation-mail-subject "Please activate your account."
   :activation-mail-body    "Please click on this link to activate your account: {{activationlink}}
Best Regards,

Your Team"
   :activation-placeholder  "{{activationlink}}"
   :smtp-data               {}                                ; passed directly to postmap like {:host "postfix"}
   :jdbc-url                db-uri
   :env                     :dev
   :registration-allowed?   true :captcha-enabled?        false
   :captcha-public-key      "your public captcha key"
   :private-recaptcha-key   "your private captcha key"
   :recaptcha-domain        "yourdomain"
   :port                    3001})



(def test-base-url (str "http://localhost:3001/"))

(defn start-browser [browser]
  (j/reset-db
    {:db       {:type :sql,
                :url  db-uri}
     :migrator migrators})
  (w/set-driver! {:browser browser}))

(defn stop-browser []
  (w/quit))

(defn server-setup [f]
  (mount/start-with {#'{{ns}}.components.config/config
                     #'{{ns}}.web.setup/test-config})
  (f)
  (mount/stop))

(defn browser-setup [f]
  (start-browser :htmlunit)
  (f)
  (stop-browser))

;; locale stuff

(def t (tower/make-t (:tconfig (get-tconfig))))