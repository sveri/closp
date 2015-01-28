(ns {{ns}}.routes.user
  (:require [compojure.core :refer :all]
    [buddy.auth :refer [authenticated? throw-unauthorized]]
    [ring.util.response :as resp]
    [noir.session :as sess]
    [noir.validation :as vali]
    [{{ns}}.layout :as layout]
    [{{ns}}.db.core :as db]))


(defn validRegister? [email pass confirm]
  (vali/rule (vali/has-value? email)
             [:id "An email address is required."])
  (vali/rule (vali/is-email? email)
             [:id "A valid email is required."])
  (vali/rule (not (db/username-exists? email))
             [:id "This username exists in the database. Please choose another one."])
  (vali/rule (vali/min-length? pass 5)
             [:pass "Password must be at least 5 characters."])
  (vali/rule (= pass confirm)
             [:confirm "Entered passwords do not match."])
  (not (vali/errors? :id :pass :confirm)))

(defn admin-page [request]
  (if-not (authenticated? request)
    (throw-unauthorized)
    (layout/render "user/admin.html")))

(defn login-page [ & [errormap]]
  (layout/render "user/login.html" errormap))

(defn login [request]
  (let [username (get-in request [:form-params "username"])
        password (get-in request [:form-params "password"] "")]
    (if-let [user (db/get-user-by-email username)]
      (try
        (cond
          (= false (:is_active user)) (login-page {:error "Please activate your account first."})
          (= false (hashers/check password (get user :pass ""))) (login-page {:error "Please provide a correct password."})
          :else (let [nexturl (get-in request [:query-params :next] "/")]
                  (sess/put! :identity (keyword username))
                  (resp/redirect nexturl)))
        (catch Exception e (timb/error e "Something messed up while logging in user: " username " with error: " e)
                           (login-page {:error "Some error occured."})))
      (login-page {:error "Please provide a correct username."}))))

(defn add-user [email password confirm sendmail?]
  (if (validRegister? email password confirm)
    (let [activationid (uservice/generate-activation-id)
          pw_crypted (hashers/encrypt password)]
      (db/create-user email pw_crypted activationid)
      (when sendmail? (uservice/send-activation-email email activationid))
      (account-created-page))
    (let [email-error (vali/on-error :id first)
          pass-error (vali/on-error :pass first)
          confirm-error (vali/on-error :confirm first)]
      (signup-page {:email-error email-error :pass-error pass-error :confirm-error confirm-error :email email}))))

(defn changepassword [oldpassword password confirm]
  )

(defroutes user-routes
           (GET "/user/admin" req (admin-page req))
           (GET "/user/login" [] (login-page))
           (POST "/user/login" req (login req))
           (GET "/user/logout" [] (logout))
           (GET "/user/signup" [] (signup-page))
           (GET "/user/accountcreated" [] (account-created-page))
           (GET "/user/activate/:id" [id] (activate-account id))
           (POST "/user/signup" [email password confirm] (add-user email password confirm true))
           (GET "/user/changepassword" [] (changepassword-page))
           (POST "/user/changepassword" [oldpassword password confirm] (changepassword oldpassword password confirm)))
