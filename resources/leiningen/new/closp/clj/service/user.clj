(ns {{ns}}.service.user
  (:require [postal.core :refer [send-message]]
            [taoensso.timbre :as timbre]
            [noir.session :as sess]))


(defmulti send-mail-by-type (fn [m _] (get m :prot)))

(defmethod send-mail-by-type :smtp [m config]
  (timbre/trace "trying to send mail to" (:data m))
  (send-message (:smtp-data config) (:data m)))

(defmethod send-mail-by-type :sendmail [m config]
  (send-message
    {:host (get-in config [:smtp-data :host] "localhost")}
    (:data m)))

(defn generate-activation-id []
  (str (java.util.UUID/randomUUID)))

(defn- generate-activation-link [activationid config]
  (str (:hostname config) "user/activate/" activationid))

(defn replace-activation [body activationid placeholder config]
  (.replace body placeholder (generate-activation-link activationid config)))

(defn get-default-mail-map [from to subject body activationid config]
  (let [body-subst (replace-activation body activationid (:activation-placeholder  config) config)]
    {:from    from
     :to      to
     :subject subject
     :body    body-subst}))

(defn send-activation-email [email activationid config]
  (try
    (send-mail-by-type {:prot (:mail-type config) :data (get-default-mail-map (:mail-from config) email
                                                                              (:activation-mail-subject config)
                                                                              (:activation-mail-body config)
                                                                              activationid config)} config)
    (timbre/info "sent activation email to: " email)
    true
    (catch Exception e (timbre/error e "Could not send email!\n"))))

(defn get-logged-in-username [] (when-let [id (sess/get :identity)] (name id)))