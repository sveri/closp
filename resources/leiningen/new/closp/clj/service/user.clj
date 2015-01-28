(ns {{ns}}.service.user
  (:require [postal.core :refer [send-message]]
    [taoensso.timbre :as timbre]
    [{{ns}}.globals :as glob]))


(defmulti send-mail-by-type (fn [m] (get m :prot)))

(defmethod send-mail-by-type :smtp [m]
  (timbre/trace "trying to send mail to" (:data m))
  (send-message glob/smtp-data (:data m)))

(defmethod send-mail-by-type :sendmail [m] (send-message {:host "localhost"} (:data m)))

(defn generate-activation-id []
  (str (java.util.UUID/randomUUID)))

(defn- generate-activation-link [activationid]
  (str glob/hostname "user/activate/" activationid))

(defn replace-activation [body activationid placeholder]
  (.replace body placeholder (generate-activation-link activationid)))

(defn get-default-mail-map [from to subject body activationid]
  (let [body-subst (replace-activation body activationid glob/activation-placeholder)]
    {:from    from
     :to      to
     :subject subject
     :body    body-subst}))

(defn send-activation-email [email activationid]
  (try
    (do (send-mail-by-type {:prot glob/mail-type :data (get-default-mail-map glob/mail-from email
                                                                             glob/activation-mail-subject
                                                                             glob/activation-mail-body activationid)})
        (timbre/info "sent activation email to: " email))
    (catch Exception e (timbre/error "Could not send email!\n" e))))