(ns {{ns}}.service.user
  (:require [{{ns}}.globals :refer [activation-mail-subject activation-mail-body activated-kw hostname
                                               mail-from mail-type activation-placeholder smtp-data]]
            [postal.core :refer [send-message]]
            [taoensso.timbre :as timbre]))


(defmulti send-mail-by-type (fn [m] (get m :prot)))

(defmethod send-mail-by-type :smtp [m]
  (timbre/trace "trying to send mail to" (:data m))
  (send-message smtp-data (:data m)))

(defmethod send-mail-by-type :sendmail [m] (send-message (:data m)))

(defn generate-activation-id []
  (str (java.util.UUID/randomUUID)))

(defn- generate-activation-link [activationid]
  (str hostname "user/activate/" activationid))

(defn replace-activation [body activationid placeholder]
  (.replace body placeholder (generate-activation-link activationid)))

(defn get-default-mail-map [from to subject body activationid]
  (let [body-subst (replace-activation body activationid activation-placeholder)]
    {:from    from
     :to      to
     :subject subject
     :body    body-subst}))

(defn send-activation-email [email activationid]
  (try
    (do (send-mail-by-type {:prot mail-type :data (get-default-mail-map mail-from email activation-mail-subject activation-mail-body activationid)})
        (timbre/info "sent activation email to: " email))
    (catch Exception e (timbre/error "Could not send email!\n" e))))

(defn get-logged-in-username [] (:username (friend/current-authentication)))

(defn is-logged-in? [] (if (get-logged-in-username) true false))

(defn is-user-activated? [usermap]
  (if (= (activated-kw usermap) true) true false))