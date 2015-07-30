(ns {{ns}}.routes.user
  (:require [compojure.core :refer [routes GET POST]]
            [buddy.hashers :as hashers]
            [ring.util.response :as resp]
            [noir.session :as sess]
            [noir.validation :as vali]
            [taoensso.timbre :as timb]
            [clojure-miniprofiler :as cjmp]
            [{{ns}}.layout :as layout]
            [{{ns}}.db.user :as db]
            [{{ns}}.service.user :as uservice]
            [{{ns}}.service.auth :as auth]
            [taoensso.tower :refer [t]])
  (:import (net.tanesha.recaptcha ReCaptchaImpl)))

(defn- connectReCaptch [recaptcha_response_field recaptcha_challenge_field priv-recaptcha-key rec-domain]
  (let [reCaptcha (new ReCaptchaImpl)]
    (.setPrivateKey reCaptcha priv-recaptcha-key)
    (.checkAnswer reCaptcha rec-domain, recaptcha_challenge_field, recaptcha_response_field)))

(defn vali-password? [pass confirm locale tconfig & [form-current-pass current-pass]]
  (vali/rule (vali/min-length? pass 5)
             [:pass (t locale tconfig :user/pass_min_length)])
  (vali/rule (= pass confirm)
             [:confirm (t locale tconfig :user/pass_match)])
  (when current-pass
    (vali/rule (hashers/check form-current-pass current-pass)
               [:oldpass (t locale tconfig :user/wrong_cur_pass)])))

(defn valid-register? [email pass confirm captcha-allowed? locale tconfig
                       & [recaptcha_response_field recaptcha_challenge_field
                          priv-recaptcha-key rec-domain]]
  (vali/rule (vali/has-value? email)
             [:id (t locale tconfig :user/email_invalid)])
  (vali/rule (vali/is-email? email)
             [:id (t locale tconfig :user/email_invalid)])
  (vali/rule (not (db/username-exists? email))
             [:id (t locale tconfig :user/username_exists)])
  (when (and captcha-allowed? recaptcha_challenge_field)
    (vali/rule (.isValid (connectReCaptch recaptcha_response_field recaptcha_challenge_field
                                          priv-recaptcha-key rec-domain))
               [:captcha (t locale tconfig :user/captcha_wrong)]))
  (vali-password? pass confirm locale tconfig)
  (not (vali/errors? :id :pass :confirm :captcha)))

(defn admin-page [params]
  (let [users (cjmp/trace "all users" (db/get-all-users (get params :filter)))]
    (layout/render "user/admin.html" (merge {:users users :roles auth/available-roles}
                                            params))))

(defn login-page [& [content]]
  (layout/render "user/login.html" content))

(defn account-created-page [locale tconfig]
  (layout/render "user/account-created.html" {:account_created_title (t locale tconfig :user/account_created_title)
                                              :account_created (t locale tconfig :user/account_created)}))

(defn account-activated-page [locale tconfig]
  (layout/render "user/account-activated.html"
                 {:account_activated_title (t locale tconfig :user/account_activated_title)
                  :account_activated (t locale tconfig :user/account_activated)}))

(defn signup-page [{:keys [captcha-public-key]} & [errormap]]
  (layout/render "user/signup.html" (merge {:captcha-public-key captcha-public-key} errormap)))

(defn activate-account [config id locale tconfig]
  (if (db/get-user-by-act-id id)
    (do (db/set-user-active id)
        (account-activated-page locale tconfig))
    (signup-page config {:email-error (t locale tconfig :user/activationid_wrong)})))

(defn changepassword-page [& [msgmap]]
  (layout/render "user/changepassword.html" msgmap))

(defn logout [] (sess/clear!) (resp/redirect "/"))

(defn login [{:keys [locale tconfig] :as request}]
  (let [username (get-in request [:form-params "username"])
        password (get-in request [:form-params "password"])
        nexturl (get-in request [:form-params "nexturl"])]
    (if-let [user (db/get-user-by-email username)]
      (try
        (cond
          (= false (:is_active user)) (login-page {:error :user/activate_account})
          (= false (hashers/check password (get user :pass ""))) (login-page
                                                                   {:error (t locale tconfig :user/pass_correct)})
          :else (do (sess/put! :role (:role user)) (sess/put! :identity username)
                    (resp/redirect (or nexturl "/"))))
        (catch Exception e (timb/error e "Something messed up while logging in user: " username " with error: " e)
                           (login-page {:error (t locale tconfig :generic/some_error)})))
      (login-page {:error (t locale tconfig :user/username_wrong)}))))

(defn send-reg-mail [sendmail? email activationid config succ-cb-page error-cb-page locale tconfig]
  (if sendmail?
    (if (uservice/send-activation-email email activationid config)
      (succ-cb-page locale tconfig)
      (error-cb-page {:email-error (t locale tconfig :user/email_failed)}))
    (succ-cb-page (layout/flash-result (t locale tconfig :user/user_added) "alert-success"))))

(defn add-user [email password confirm sendmail? succ-cb-page error-cb-page config locale tconfig
                & [recaptcha_response_field recaptcha_challenge_field priv-recaptcha-key rec-domain]]
  (if (valid-register? email password confirm (:captcha-allowed? config) locale tconfig
                       recaptcha_response_field recaptcha_challenge_field
                       priv-recaptcha-key rec-domain)
    (let [activationid (uservice/generate-activation-id)
          pw_crypted (hashers/encrypt password)]
      (db/create-user email pw_crypted activationid)
      (send-reg-mail sendmail? email activationid config succ-cb-page error-cb-page locale tconfig))
    (let [email-error (vali/on-error :id first)
          pass-error (vali/on-error :pass first)
          confirm-error (vali/on-error :confirm first)
          captcha-error (vali/on-error :captcha first)]
      (error-cb-page config {:email-error   email-error :pass-error pass-error :confirm-error confirm-error :email email
                             :captcha-error captcha-error}))))

(defn changepassword [oldpassword password confirm locale tconfig]
  (let [user (db/get-user-by-email (uservice/get-logged-in-username))]
    (vali-password? password confirm locale tconfig oldpassword (:pass user))
    (if (not (vali/errors? :oldpass :pass :confirm))
      (do (db/change-password (:email user) (hashers/encrypt password))
          (changepassword-page (layout/flash-result (t locale tconfig :user/pass_changed) "alert-success")))
      (let [old-error (vali/on-error :oldpass first)
            pass-error (vali/on-error :pass first)
            confirm-error (vali/on-error :confirm first)]
        (changepassword-page {:pass-error pass-error :confirm-error confirm-error :old-error old-error})))))

(defn really-delete-page [uuid]
  (layout/render "user/reallydelete.html" {:uuid uuid :username (:email (db/get-user-by-uuid uuid))}))

(defn really-delete [uuid delete_cancel locale tconfig]
  (if (= delete_cancel "Cancel")
    (do (layout/flash-result (t locale tconfig :generic/deletion_canceled) "alert-warning")
        (resp/redirect "/admin/users"))
    (do (db/delete-user uuid)
        (layout/flash-result (t locale tconfig :user/deleted) "alert-success")
        (resp/redirect "/admin/users"))))

(defmulti update-user (fn [update_delete _ _ _ _ _] update_delete))
(defmethod update-user "Update" [_ user-uuid role active locale tconfig]
  (let [role (if (= "none" role) "" role)
        act (= "on" active)]
    (db/update-user user-uuid {:role role :is_active act}))
  (let [user (db/get-user-by-uuid user-uuid)]
    (admin-page (layout/flash-result (t locale tconfig :user/updated (:email user)) "alert-success"))))

(defmethod update-user "Delete" [_ user-uuid _ _ _ _]
  (really-delete-page user-uuid))

(defn user-routes [config]
  (routes
    (GET "/user/login" [next] (login-page {:nexturl next}))
    (POST "/user/login" req (login req))
    (GET "/user/logout" [] (logout))
    (GET "/user/changepassword" [] (changepassword-page))
    (POST "/user/changepassword" [oldpassword password confirm :as req]
          (changepassword oldpassword password confirm (:locale req) (:tconfig req)))
    (POST "/admin/user/update" [user-uuid role active update_delete :as req]
          (update-user update_delete user-uuid role active (:locale req) (:tconfig req)))
    (POST "/admin/user/delete" [user-uuid delete_cancel :as req]
          (really-delete user-uuid delete_cancel (:locale req) (:tconfig req)))
    (POST "/admin/user/add" [email password confirm :as req]
          (add-user email password confirm false admin-page admin-page config (:locale req) (:tconfig req)))
    (GET "/admin/users" [filter] (admin-page {:filter filter}))))

(defn registration-routes [config]
  (routes
    (GET "/user/accountcreated" req (account-created-page (:locale req) (:tconfig req)))
    (GET "/user/activate/:id" [id :as req] (activate-account config id (:locale req) (:tconfig req)))
    (POST "/user/signup" [email password confirm recaptcha_response_field recaptcha_challenge_field :as req]
          (add-user email password confirm true account-created-page signup-page config (:locale req) (:tconfig req)
                    recaptcha_response_field recaptcha_challenge_field (:private-recaptcha-key config)
                    (:recaptcha-domain config)))
    (GET "/user/signup" [] (signup-page config))))