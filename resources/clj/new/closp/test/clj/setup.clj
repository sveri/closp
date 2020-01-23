(ns {{namespace}}.frontend.setup
  (:require [etaoin.api :as eta]
            [com.stuartsierra.component :as component]
            [taoensso.tempura :refer [tr]]
            [system.repl :refer [go stop] :as repl]
            [clojure.java.jdbc :as j]
            [{{namespace}}.components.server :refer [new-web-server]]
            [{{namespace}}.components.handler :refer [new-handler]]
            [{{namespace}}.components.config :as c]
            [{{namespace}}.components.db :refer [new-db]]
            [{{namespace}}.locale :as l]))

(def db-uri "jdbc:postgresql://localhost:5432/{{name}}_test?user={{name}}&password={{name}}")
(def db {:connection-uri db-uri})

(def ^:dynamic *driver*)

; custom config for test configuration
(def test-config
  {:jdbc-url                db-uri
   :env                     :dev
   :registration-allowed?   true
   :captcha-enabled?        false
   :captcha-public-key      "your public captcha key"
   :private-recaptcha-key   "your private captcha key"
   :recaptcha-domain        "yourdomain"
   :port                    3001})


(defn test-system []
  (component/system-map
    :config (c/new-config test-config)
    :db (component/using (new-db) [:config])
    :handler (component/using (new-handler) [:config :db])
    :web (component/using (new-web-server) [:handler :config])))

(def test-base-url (str "http://localhost:3001/"))

(defn logout [driver]
  (eta/go driver (str test-base-url "user/logout")))

(defn start-server []
  (repl/set-init! #'test-system)
  (go))

(defn stop-server []
  (stop))

(defn server-setup [f]
  (start-server)
  (f)
  (stop-server))

(defn browser-setup [f]
  ;(eta/with-firefox {} driver
  (eta/with-firefox-headless {} driver
   (binding [*driver* driver]
     (j/execute! db ["drop table if exists users;"])
     (j/execute! db ["CREATE TABLE users ( id bigserial NOT NULL PRIMARY KEY, email text UNIQUE NOT NULL,
                      displayname text NOT NULL, role text NOT NULL, last_login time, is_active BOOLEAN DEFAULT FALSE
                      NOT NULL, password text);"])
     (j/insert! db :users {:email "admin@localhost.de" :displayname "admin" :is_active true :role "admin"
                           :password "bcrypt+sha512$d6d175aaa9c525174d817a74$12$24326124313224314d345444356149457a67516150447967517a67472e717a2e777047565a7071495330625441704f46686a556b5535376849743575"})

     (f)
     (logout *driver*))))

;; locale stuff

(def t (partial tr
                {:default-locale :en
                 :dict           l/local-dict}
                ["en"]))
