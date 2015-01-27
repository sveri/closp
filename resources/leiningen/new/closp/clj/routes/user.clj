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
        password (get-in request [:form-params "password"])]
    (if-let [user (db/get-user-by-email username)]
      (if (= (:pass user) password)
        (let [nexturl (get-in request [:query-params :next] "/")]
          (sess/put! :identity (keyword username))
          (resp/redirect nexturl))
        (login-page {:error "Please provide a correct password."})))))

(defn logout [] (sess/clear!) (resp/redirect "/"))

(defn signup []
  (layout/render "user/signup.html"))



(defn add-user [email password confirm ]
  (if (validRegister? email password confirm)
    (do
      (let [activationid (userservice/generate-activation-id)
            pw_crypted (creds/hash-bcrypt password)]
        (do
          (globals/create-user storage email pw_crypted globals/new-user-role activationid)
          (if (and send_email globals/send-activation-email)
            (userservice/send-activation-email email activationid))
          (when signup-succ-func (signup-succ-func))))
      (resp/redirect succ-page))
    (let [email-error (vali/on-error :id first)
          pass-error (vali/on-error :pass first)
          confirm-error (vali/on-error :confirm first)]
      (error-page {:email-error email-error :pass-error pass-error :confirm-error confirm-error :email email}))))

(defroutes user-routes
           (GET "/user/admin" req (admin-page req))
           (GET "/user/login" [] (login-page))
           (POST "/user/login" req (login req))
           (GET "/user/logout" [] (logout))
           (GET "/user/signup" [] (signup))
           (POST "/user/signup" [email password confirm] (add-user email password confirm)))
