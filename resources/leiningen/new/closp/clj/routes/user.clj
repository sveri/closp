(ns {{ns}}.routes.user
  (:require [compojure.core :refer [routes GET POST]]
            [buddy.hashers :as hashers]
            [ring.util.http-response :as resp]
            [clojure.tools.logging :as log]
            [phrase.alpha :as pa]
            [buddy.sign.jwt :as jwt]
            [{{ns}}.db.user :as db]
            [{{ns}}.cljc.validation.user :as vu]
            [{{ns}}.service.user :as s-u]))


(defn sign-token [email config]
  (jwt/sign {:user-id email} (:jwt-secret config)))

(defn login-handler [config email password db]
  (if-let [user (db/get-user-by-email db email)]
    (cond
      (or (= 0 (:is_active user)) (= false (:is_active user)))
      (do (log/info "Inactive user tried to access /api/login: " email)
          (resp/unauthorized {:error "Unauthorized"}))

      (= false (hashers/check password (get user :pass "")))
      (do (log/info "Wrong password tried to access /api/login: " email)
          (resp/unauthorized {:error "Unauthorized"}))

      :else (resp/ok (merge (dissoc user :pass) {:token (sign-token email config)})))

    (do (log/info "Non existent user tried to access /api/login: " email)
        (resp/unauthorized {:error "Unauthorized"}))))


(defn valid-register? [email password db]
  (let [username-exists? (db/username-exists? db email)
        email-invalid (pa/phrase-first {} ::vu/email (or email ""))
        password-invalid (pa/phrase-first {} ::vu/password (or password ""))]
    (when (or username-exists? email-invalid password-invalid)
      (merge {}
             (when username-exists? {:username-exists username-exists?})
             (when email-invalid {:email-invalid email-invalid})
             (when password-invalid {:password-invalid password-invalid})))))



(defn signup-user [email password config db]
  (if-let [errors (valid-register? email password db)]
    (resp/internal-server-error errors)
    (let [bcrypted-pw (hashers/encrypt password)]
      (db/create-user db email bcrypted-pw)
      (resp/ok {:token (sign-token email config)
                :email email :role "none" :is_active true}))))




(defn valid-changepassword? [current-password new-password user]
  (let [current-password-invalid (hashers/check (or current-password "") (:pass user))
        password-invalid (pa/phrase-first {} ::vu/password (or new-password ""))]
    (when (or (not current-password-invalid) password-invalid)
      (merge {}
             (when (not current-password-invalid) {:current-password-invalid current-password-invalid})
             (when password-invalid {:password-invalid password-invalid})))))

(defn changepassword [current-password new-password db req]
  (let [user (db/get-user-by-email db (s-u/get-user-id-from-req req))
        errors (valid-changepassword? current-password new-password user)]
    (if errors
      (resp/internal-server-error errors)
      (do (db/change-password db (:email user) (hashers/encrypt new-password))
          (resp/ok)))))

(defn user-routes [config db]
  (routes
    (POST "/api/user/login" [email password] (login-handler config email password db))
    (POST "/api/user/changepassword" [current-password password :as req]
          (changepassword current-password password db req))
    (POST "/api/user/signup" [email password] (signup-user email password config db))))
