(ns {{ns}}.service.user
  (:require [postal.core :refer [send-message]]
            [taoensso.timbre :as timbre]
            [noir.session :as sess]
            [clojure.core.typed :as t]
            [{{ns}}.types :as ty]
            [clojure.spec :as s]))

(defmulti send-mail-by-type (fn [m _] (get m :prot)))

(defmethod send-mail-by-type :smtp [m config]
  (timbre/trace "trying to send mail to" (:data m))
  (send-message (:smtp-data config) (:data m)))

(defmethod send-mail-by-type :sendmail [m config]
  (send-message
    {:host (get-in config [:smtp-data :host] "localhost")}
    (:data m)))

(defmethod send-mail-by-type :test [_ _] true)

(t/ann ^:no-check get-logged-in-user [-> ty/user])
(defn get-logged-in-user
  "Needs a logged in user to retrieve the user name and role, otherwise returns empty string map"
  []
  (try {:email (sess/get :identity "")
        :role (sess/get :role "")}
       (catch Exception _ {:email "" :role ""})))

(s/fdef get-logged-in-username :ret string?)
(defn get-logged-in-username [] (:email (get-logged-in-user)))
