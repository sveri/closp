(ns {{ns}}.routes.user
  (:require [compojure.core :refer [routes GET POST]]
            [buddy.hashers :as hashers]
            [ring.util.response :as resp]
            [noir.session :as sess]
            [noir.validation :as vali]
            [clojure.tools.logging :as log]
            [{{ns}}.views.user :as vh]
            [{{ns}}.views.base :as v]
            [{{ns}}.db.user :as db]
            [{{ns}}.service.auth :as auth]
            [{{ns}}.service.user :as uservice])
  (:import (net.tanesha.recaptcha ReCaptchaImpl)))

(defn- connectReCaptch [recaptcha_response_field recaptcha_challenge_field priv-recaptcha-key rec-domain]
  (let [reCaptcha (new ReCaptchaImpl)]
    (.setPrivateKey reCaptcha priv-recaptcha-key)
    (.checkAnswer reCaptcha rec-domain, recaptcha_challenge_field, recaptcha_response_field)))

(defn vali-password? [pass confirm localize & [form-current-pass current-pass]]
  (vali/rule (vali/min-length? pass 5)
             [:pass (localize [:user/pass_min_length])])
  (vali/rule (= pass confirm)
             [:confirm (localize [:user/pass_match])])
  (when current-pass
    (vali/rule (hashers/check form-current-pass current-pass)
               [:oldpass (localize [:user/wrong_cur_pass])])))

(defn valid-register? [email pass confirm captcha-enabled? localize db
                       & [recaptcha_response_field recaptcha_challenge_field
                          priv-recaptcha-key rec-domain]]
  (vali/rule (vali/has-value? email)
             [:id (localize [:user/email_invalid])])
  (vali/rule (vali/is-email? email)
             [:id (localize [:user/email_invalid])])
  (vali/rule (not (db/username-exists? db email))
             [:id (localize [:user/username_exists])])
  (when (and captcha-enabled? recaptcha_challenge_field)
    (vali/rule (.isValid (connectReCaptch recaptcha_response_field recaptcha_challenge_field
                                          priv-recaptcha-key rec-domain))
               [:captcha (localize [:user/captcha_wrong])]))
  (vali-password? pass confirm localize)
  (not (vali/errors? :id :pass :confirm :captcha)))

(defn admin-page [params {:keys [localize] :as req} db]
  (let [users (db/get-all-users db (get params :filter))
        users-cleaned (map #(assoc % :is_active (if (or (= (:is_active %) false) (= (:is_active %) 0)) false true)) users)]
    (vh/admin-page (merge {:users       users-cleaned :roles auth/available-roles
                           :admin_title (localize [:admin/title])}
                          params)
                   req)))

(defn login-page [content req]
  (vh/login-page content req))

(defn signup-page [{:keys [captcha-public-key captcha-enabled?]} errormap req]
  (vh/signup-page (merge {:captcha-public-key captcha-public-key
                          :captcha-enabled?   captcha-enabled?}
                         errormap)
                  req))

(defn changepassword-page [msg req]
  (vh/changepassword-page msg req))


(defn logout [] (sess/clear!) (resp/redirect "/"))

(defn update-session-and-redirect [user nexturl]
  (sess/put! :role (:role user))
  (sess/put! :identity (:email user))
  (sess/put! :user-id (:id user))
  (resp/redirect (or nexturl "/")))

(defn login [{:keys [localize] :as request} db]
  (let [username (get-in request [:form-params "username"])
        password (get-in request [:form-params "password"])
        nexturl (get-in request [:form-params "nexturl"])]
    (if-let [user (db/get-user-by-email db username)]
      (try
        (cond
          (= false (hashers/check password (get user :pass ""))) (login-page
                                                                   {:error (localize [:user/pass_correct])} request)
          :else (update-session-and-redirect user nexturl))
        (catch Exception e (log/error "Something messed up while logging in user: " username)
                           (.printStackTrace e)
                           (login-page {:error (localize [:generic/some_error])} request)))
      (login-page {:error (localize [:user/username_wrong])} request))))


(defn changepassword [oldpassword password confirm {:keys [localize] :as req} db]
  (let [user (db/get-user-by-email db (uservice/get-logged-in-username))]
    (vali-password? password confirm localize oldpassword (:pass user))
    (if (not (vali/errors? :oldpass :pass :confirm))
      (do (db/change-password db (:email user) (hashers/encrypt password))
          (v/flash-result (localize [:user/pass_changed]) "alert-success")
          (changepassword-page {} req))
      (let [old-error (vali/on-error :oldpass first)
            pass-error (vali/on-error :pass first)
            confirm-error (vali/on-error :confirm first)]
        (changepassword-page {:pass-error pass-error :confirm-error confirm-error :old-error old-error} req)))))

(defn really-delete-page [id db req]
  (vh/really-delete-page {:id id :username (:email (db/get-user-by-id db (read-string id)))} req))

(defn really-delete [id delete_cancel localize db]
  (if (= delete_cancel (localize [:generic/cancel]))
    (do (v/flash-result (localize [:generic/deletion_canceled]) "alert-warning")
        (resp/redirect "/admin/users"))
    (do (db/delete-user db (read-string id))
        (v/flash-result (localize [:user/deleted]) "alert-success")
        (resp/redirect "/admin/users"))))

(defn update-user [update_delete user-id role active {:keys [localize] :as req} db]
  (if (= update_delete (localize [:admin/update]))
    (let [act (= "on" active)
          user-id-int (read-string user-id)]
      (db/update-user db user-id-int {:role role :is_active act})
      (let [user (db/get-user-by-id db user-id-int)]
        (admin-page (v/flash-result (localize [:user/updated] [(:email user)]) "alert-success") req db)))
    (really-delete-page user-id db req)))

(defn- user-form-errors [email]
  {:email-error   (vali/on-error :id first) :pass-error (vali/on-error :pass first)
   :confirm-error (vali/on-error :confirm first) :email email
   :captcha-error (vali/on-error :captcha first)})

(defn- create-new-user! [email password db]
  (db/create-user db email (hashers/encrypt password)))

(defn admin-add-user [email password confirm config {:keys [localize] :as req} db]
  (if (valid-register? email password confirm (:captcha-enabled? config) localize db)
    (do (create-new-user! email password db)
        (v/flash-result (localize [:user/user_added]) "alert-success")
        (admin-page {} req db))
    (admin-page (user-form-errors email) req db)))

(defn signup-user [email password confirm {:keys [recaptcha-domain private-recaptcha-key] :as config} {:keys [localize] :as req}
                   response_field challenge_field db]
  (if (valid-register? email password confirm (:captcha-enabled? config) localize db
                       response_field challenge_field
                       private-recaptcha-key recaptcha-domain)
    (do (create-new-user! email password db)
        (update-session-and-redirect (db/get-user-by-email db email) nil))
    (signup-page config (user-form-errors email) req)))

(defn user-routes [config db]
  (routes
    (GET "/user/login" [nexturl :as req] (login-page {:nexturl nexturl} req))
    (POST "/user/login" req (login req db))
    (GET "/user/logout" [] (logout))
    (GET "/user/changepassword" req (changepassword-page {} req))
    (POST "/user/changepassword" [oldpassword password confirm :as req]
          (changepassword oldpassword password confirm req db))
    (POST "/admin/user/update" [user-id role active update_delete :as req]
          (update-user update_delete user-id role active req db))
    (POST "/admin/user/delete" [user-id delete_cancel :as req]
          (really-delete user-id delete_cancel (:localize req) db))
    (POST "/admin/user/add" [email password confirm :as req]
          (admin-add-user email password confirm config req db))
    (GET "/admin/users" [filter :as req] (admin-page {:filter filter} req db))))

(defn registration-routes [config db]
  (routes
    (POST "/user/signup" [email password confirm recaptcha_response_field recaptcha_challenge_field :as req]
          (signup-user email password confirm config req
                       recaptcha_response_field recaptcha_challenge_field db))
    (GET "/user/signup" req (signup-page config {} req))))
