(ns {{ns}}.web.setup
  (:require [reloaded.repl :refer [go stop]]
            [clj-webdriver.taxi :as w]
            [com.stuartsierra.component :as component]
            [joplin.core :as j]
            [taoensso.tower :as tower]
            [{{ns}}.components.server :refer [new-web-server new-web-server-prod]]
            [{{ns}}.components.handler :refer [new-handler]]
            [{{ns}}.components.config :as c]
            [{{ns}}.components.db :refer [new-db]]
            [{{ns}}.components.components :refer [prod-system]]
            [{{ns}}.components.locale :as l]))

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
   :registration-allowed?   true
   :captcha-enabled?        false
   :captcha-public-key      "your public captcha key"
   :private-recaptcha-key   "your private captcha key"
   :recaptcha-domain        "yourdomain"
   :port                    3001})


(defn test-system []
  (component/system-map
    :locale (l/new-locale)
    :config (c/new-config test-config)
    :db (component/using (new-db) [:config])
    :handler (component/using (new-handler) [:config :locale])
    :web (component/using (new-web-server) [:handler :config])))

(def test-base-url (str "http://localhost:3001/"))

(defn start-browser [browser]
  (j/migrate-db
    {:db       {:type :sql,
                :url  db-uri}
     :migrator migrators})
  (w/set-driver! {:browser browser}))

(defn stop-browser []
  (j/rollback-db
    {:db       {:type :sql,
                :url  db-uri}
     :migrator migrators})
  (w/quit))

(defn start-server []
  (reloaded.repl/set-init! test-system)
  (go))

(defn stop-server []
  (stop))

;; locale stuff

(def t (tower/make-t l/tconfig))