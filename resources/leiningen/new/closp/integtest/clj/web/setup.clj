(ns {{ns}}.web.setup
  (:require [{{ns}}.components.components :refer [prod-system]]
            [reloaded.repl :refer [go stop]]
            [clj-webdriver.taxi :as w]
            [com.stuartsierra.component :as component]
            [{{ns}}.components.server :refer [new-web-server new-web-server-prod]]
            [{{ns}}.components.handler :refer [new-handler]]
            [{{ns}}.components.config :as c]
            [{{ns}}.components.db :refer [new-db]]
            [joplin.core :as j]
            [foo.bar.components.locale :as l]))

(def db-uri "jdbc:sqlite:./db/{{name}}-integ-test.sqlite")
(def migrators "resources/migrators/sqlite")

; custom config for configuration
(def test-config
  {:hostname                "http://localhost/"
   :mail-from               "info@localhost.de"
   :mail-type               :sendmail
   :activation-mail-subject "Please activate your account."
   :activation-mail-body    "Please click on this link to activate your account: {{activationlink}}
Best Regards,

Your Team"
   :activation-placeholder  "{{activationlink}}"
   :smtp-data               {}                                ; passed directly to postmap like {:host "postfix"}
   :jdbc-url                "jdbc:sqlite:./db/{{name}}.sqlite"
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
  (w/set-driver! {:browser browser}))

(defn stop-browser []
  (w/quit))

(defn start-server []
  (j/migrate-db
    {:db       {:type :sql,
                :url  db-uri}
     :migrator migrators})
  (reloaded.repl/set-init! test-system)
  (go))

(defn stop-server []
  (stop)
  (j/rollback-db
    {:db       {:type :sql,
                :url  db-uri}
     :migrator migrators}))

(defn server-setup [f]
  (start-server)
  (f)
  (stop-server))

(defn browser-setup [f]
  (start-browser :htmlunit)
  (f)
  (stop-browser))
