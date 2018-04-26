(ns {{ns}}.user.events
  (:require [re-frame.core :as rf]
            [phrase.alpha :as pa]
            [com.degel.re-frame.storage]
            [{{ns}}.third-party.http-fx]
            [{{ns}}.cljc.validation.user :as v-u]
            [{{ns}}.cljc.locale :as loc]
            [{{ns}}.localstorage :as ls]
            [{{ns}}.common :as comm]
            [{{ns}}.helper :as h]))



(rf/reg-event-db
  ::set-signup-email
  (rf/path :signup)
  (fn [db [_ email]]
    (assoc db :email email :email-validation (pa/phrase-first {} ::v-u/email email))))

(rf/reg-event-db
  ::set-login-email
  (rf/path :login)
  (fn [db [_ email]]
    (assoc db :email email)))


(defn do-passwords-match [p1 p2]
  (if (not= p1 p2) (loc/localize [:user/pass_match]) nil))

(rf/reg-event-db
  ::set-signup-password
  (rf/path :signup)
  (fn [db [_ password]]
    (assoc db :password password :password-validation (pa/phrase-first {} ::v-u/password password)
              :password-confirmation-validation (do-passwords-match password (-> db :password-confirmation)))))

(rf/reg-event-db
  ::set-login-password
  (rf/path :login)
  (fn [db [_ password]]
    (assoc db :password password)))

(rf/reg-event-db
  ::set-signup-password-confirmation
  (rf/path :signup)
  (fn [db [_ password-confirmation]]
    (assoc db :password-confirmation password-confirmation
              :password-confirmation-validation (do-passwords-match password-confirmation (-> db :password)))))


(rf/reg-event-fx
  ::signup
  (fn [{:keys [db]} [_ _]]
    (let [email (h/clean-input (-> db :signup :email))
          password (h/clean-input (-> db :signup :password))]
      {:http-xhrio {:method     :post
                    :params     {:email email :password password}
                    :uri        "/api/user/signup"
                    :on-success [::user-loggedin-signedup]
                    :on-failure [::generic-signup-error]}
       :db         (comm/show-loading-screen db)})))


(rf/reg-event-fx
  ::user-loggedin-signedup
  (fn [{db :db} [_ response]]
    {:storage/set {:session? false
                   :name ls/local-storage-jwt-token-key-with-ns :value (:token response)}
     :db (-> db
             (dissoc :login)
             (dissoc :signup)
             (assoc :user (dissoc response :token))
             (comm/hide-loading-screen))
     :navigate-to :home}))



(rf/reg-event-db
  ::generic-signup-error
  (fn [db [_ {:keys [response]}]]
    (->
      (cond
        (contains? response :email-invalid) (assoc-in db [:signup :email-validation] (loc/localize [:user/email_invalid]))
        (contains? response :username-exists) (assoc-in  db [:signup :email-validation] (loc/localize [:user/username_exists]))
        (contains? response :password-invalid) (assoc-in  db [:signup :password-validation] (loc/localize [:user/pass_min_length]))
        :default db)
      (comm/hide-loading-screen))))


(rf/reg-event-fx
  ::login
  (fn [{:keys [db]} [_ _]]
    (let [email (h/clean-input (-> db :login :email))
          password (h/clean-input (-> db :login :password))]
      {:http-xhrio {:method     :post
                    :params     {:email email :password password}
                    :uri        "/api/user/login"
                    :on-success [::user-loggedin-signedup]
                    :on-failure [::generic-login-error]}
       :db         (comm/show-loading-screen db)})))


(rf/reg-event-fx
  ::logout
  (fn [{db :db} _]
    {:storage/remove {:name ls/local-storage-jwt-token-key-with-ns}
     :db (-> db
             (dissoc :user))
     :navigate-to :login}))




(rf/reg-event-db
  ::generic-login-error
  (fn [db _]
    (-> db
        (assoc-in  [:login :generic-error] (loc/localize [:user/login_failed]))
        (comm/hide-loading-screen))))



(rf/reg-event-db
  ::store-user-from-initial-data
  (fn [db [_ response]]
    (-> db
        (assoc :user (:user response)))))


; changepassword



(rf/reg-event-db
  ::set-cp-current-password
  (rf/path :cp)
  (fn [db [_ password]]
    (assoc db :current-password password :current-password-validation (pa/phrase-first {} ::v-u/password password))))

(rf/reg-event-db
  ::set-cp-password
  (rf/path :cp)
  (fn [db [_ password]]
    (assoc db :password password :password-validation (pa/phrase-first {} ::v-u/password password)
           :password-confirmation-validation (do-passwords-match password (-> db :password-confirmation)))))

(rf/reg-event-db
  ::set-cp-password-confirmation
  (rf/path :cp)
  (fn [db [_ password]]
    (assoc db :password-confirmation password :password-confirmation-validation (do-passwords-match password (-> db :password)))))



(rf/reg-event-fx
  ::change-password
  (fn [{:keys [db]} [_ _]]
    (let [current-password (h/clean-input (-> db :cp :current-password))
          password (h/clean-input (-> db :cp :password))]
      {:http-xhrio {:method     :post
                    :params     {:current-password current-password :password password}
                    :uri        "/api/user/changepassword"
                    :on-success [::password-changed]
                    :on-failure [::generic-cp-error]}
       :db         (comm/show-loading-screen db)})))


(rf/reg-event-fx
  ::password-changed
  (fn [{db :db} _]
    {:navigate-to :home
     :db (-> db
             (dissoc :cp)
             (comm/hide-loading-screen)
             (comm/set-generic-success-message (loc/localize [:user/pass_changed])))}))

(rf/reg-event-db
  ::generic-cp-error
  (fn [db [_ {:keys [response]}]]
    (let [error (if (contains? response :current-password-invalid)
                  (loc/localize [:user/pass_match])
                  (loc/localize [:generic/error]))]
      (-> db
        (assoc :generic-error error)
        (comm/hide-loading-screen)))))