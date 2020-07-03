(ns {{namespace}}.routes.user
  (:require [compojure.core :refer [routes GET POST]]
            [buddy.hashers :as hashers]
            [ring.util.response :as resp]
            [clojure.tools.logging :as log]
            [org.httpkit.client :as http]
            [{{namespace}}.views.user :as vh]
            [{{namespace}}.db.user :as db]
            [clojure.string :as str]
            [clojure.data.json :as js]
            [{{namespace}}.service.auth :as auth]
            [{{namespace}}.service.user-service :as uservice]))


(defn admin-page
  ([params req db] (admin-page params req db nil))
  ([params req db toast]
   (let [users (db/get-all-users db (get params :filter))
         users-cleaned (map #(assoc % :is_active (if (or (= (:is_active %) false) (= (:is_active %) 0)) false true)) users)]
     (vh/admin-page (merge {:users  users-cleaned :roles auth/available-roles :filter filter} params)
                    req toast))))

(defn signup-page [{:keys [captcha-public-key captcha-enabled?]} errormap req]
  (vh/signup-page (merge {:captcha-public-key captcha-public-key
                          :captcha-enabled?   captcha-enabled?}
                         errormap)
                  req))

(defn logout []
  (assoc (resp/redirect "/") :session nil))

(defn update-session-and-redirect [user nexturl]
  (-> (resp/redirect (or nexturl "/") :see-other)
      (assoc-in [:session :user :displayname] (:displayname user))
      (assoc-in [:session :user :email] (:email user))
      (assoc-in [:session :user :role] (:role user))
      (assoc-in [:session :user :id] (:id user))))


(defn login [{:keys [localize] :as req} db]
  (let [email (get-in req [:form-params "email"])
        password (get-in req [:form-params "password"])
        nexturl (get-in req [:form-params "nexturl"])]
    (if-let [user (db/get-user-by-email db email)]
      (try
        (cond
          (= false (hashers/check password (get user :password "")))
          (vh/login-page {:error (localize [:user/pass_correct])} req)

          (= false (:is_active user))
          (vh/login-page {:error (localize [:user/inactive])} req)

          :else (update-session-and-redirect user nexturl))
        (catch Exception e (log/error "Something messed up while logging in user: " email)
                           (.printStackTrace e)
                           (vh/login-page {:error (localize [:generic/some_error])} req)))
      (vh/login-page {:error (localize [:user/email_wrong])} req))))

(defn validate-change-password [user oldpassword password confirm localize]
  (let [old-passwords-match? (hashers/check oldpassword (get user :password ""))
        password-invalid? (< (count password) vh/password-min-length)
        confirm-invalid? (not (= password confirm))]
    (merge {}
           (when-not old-passwords-match? {:old-error (localize [:user/wrong_cur_pass])})
           (when password-invalid? {:password-error (localize [:user/pass_min_length] [vh/password-min-length])})
           (when confirm-invalid? {:confirm-error (localize [:user/pass_match])}))))

(defn changepassword [oldpassword password confirm {:keys [localize] :as req} db]
  (let [user (db/get-user-by-email db (uservice/get-loggedin-email-address req))
        validation-errors (validate-change-password user oldpassword password confirm localize)]
    (if (empty? validation-errors)
      (do (db/change-password db (:email user) (hashers/encrypt password))
          (-> (resp/redirect "/user/changepassword" :see-other)
              (assoc :flash {:toast {:text (localize [:user/pass_changed]) :classes "green lighten1"}})))
      (vh/changepassword-page validation-errors req))))

(defn really-delete-page [user-email req]
  (vh/really-delete-page {:email user-email} req))

(defn really-delete [user-email delete_cancel {:keys [localize] :as req} db]
  (if (= delete_cancel (localize [:generic/cancel]))
    (resp/redirect "/admin/users" :see-other)
    (do (db/delete-user db user-email)
        (-> (resp/redirect "/admin/users" :see-other)
            (assoc :flash {:toast {:text (localize [:user/deleted] [user-email]) :classes "green lighten1"}})))))

(defn update-user [update_delete user-email role active {:keys [localize] :as req} db]
  (if (= update_delete (localize [:admin/update]))
    (let [act (= "on" active)]
      (db/update-user db user-email {:role role :is_active act})
      (-> (resp/redirect "/admin/users" :see-other)
          (assoc :flash {:toast {:text (localize [:user/updated] [user-email]) :classes "green lighten1"}})))
    (really-delete-page user-email req)))

(defn- create-new-user! [email password displayname db]
  (db/create-user db email displayname (hashers/encrypt password)))

(defn re-captcha-invalid? [re-captcha-token priv-recaptcha-key]
  (let [resp @(http/post "https://www.google.com/recaptcha/api/siteverify"
                         {:form-params {:secret   priv-recaptcha-key
                                        :response re-captcha-token}})
        body (js/read-json (:body resp))]
    (not (:success body))))


(defn validate-signup
  ([email password localize db] (validate-signup email password localize db false nil nil))
  ([email password localize db captcha-enabled? re-captcha-token priv-recaptcha-key]
   (let [email-exists? (db/email-exists? db email)
         password-invalid? (< (count password) vh/password-min-length)
         email-invalid? (or (not (str/includes? email ".")) (not (str/includes? email "@")))
         re-captcha-invalid? (when (and captcha-enabled? (re-captcha-invalid? re-captcha-token priv-recaptcha-key)))]
     (merge {}
            (when email-exists? {:email-exists (localize [:user/email_exists])})
            (when password-invalid? {:password-error (localize [:user/pass_min_length] [vh/password-min-length])})
            (when email-invalid? {:email-invalid (localize [:user/email_invalid])})
            (when re-captcha-invalid? {:re-captcha-invalid (localize [:user/captcha_wrong])})))))


(defn admin-add-user [email password {:keys [localize] :as req} displayname db]
  (let [validation-errors (validate-signup email password localize db)]
    (if (empty? validation-errors)
      (do (create-new-user! email password displayname db)
          (-> (resp/redirect "/admin/users" :see-other)
              (assoc :flash {:toast {:text (localize [:user/user_added]) :classes "green lighten1"}})))
      (admin-page (merge validation-errors {:email email :displayname displayname}) req db))))

(defn signup-user [db config
                   {{:keys [email password displayname re-captcha-token localize] } :params :as req}]
  (let [validation-errors (validate-signup email password localize db
                                           (:captcha-enabled? config) re-captcha-token
                                           (:private-recaptcha-key config))]
    (if (empty? validation-errors)
      (do (create-new-user! email password displayname db)
          (update-session-and-redirect (db/get-user-by-email db email) nil))
      (signup-page config (merge validation-errors
                                 {:displayname displayname
                                  :email       email})
                   req))))

(defn user-routes [config db]
  (routes
    (GET "/user/login" [nexturl :as req] (vh/login-page {:nexturl nexturl} req))
    (POST "/user/login" req (login req db))
    (GET "/user/logout" [] (logout))
    (GET "/user/changepassword" req (vh/changepassword-page {} req))
    (POST "/user/changepassword" [oldpassword password confirm :as req]
      (changepassword oldpassword password confirm req db))
    (POST "/user/signup" req (signup-user db config req))
    (GET "/user/signup" req (signup-page config {} req))
    (POST "/admin/user/update" [user-email role active update_delete :as req]
      (update-user update_delete user-email role active req db))
    (POST "/admin/user/delete" [user-email delete_cancel :as req]
      (really-delete user-email delete_cancel req db))
    (POST "/admin/user/add" [email password displayname :as req]
      (admin-add-user email password req displayname db))
    (GET "/admin/users" [filter :as req] (admin-page {:filter filter} req db))))
