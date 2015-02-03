(ns {{ns}}.routes.user
  (:require [compojure.core :refer :all]
    [buddy.hashers :as hashers]
    [ring.util.response :as resp]
    [noir.session :as sess]
    [noir.validation :as vali]
    [taoensso.timbre :as timb]
    [{{ns}}.layout :as layout]
    [{{ns}}.db.core :as db]
    [{{ns}}.service.user :as uservice]))

(def ^:const available-roles ["admin" "none"])

(defn vali-password? [pass confirm & [form-current-pass current-pass]]
  (vali/rule (vali/min-length? pass 5)
             [:pass "Password must be at least 5 characters."])
  (vali/rule (= pass confirm)
             [:confirm "Entered passwords do not match."])
  (when current-pass
    (vali/rule (hashers/check form-current-pass current-pass)
               [:oldpass "Current password was incorrect."])))

(defn valid-register? [email pass confirm]
  (vali/rule (vali/has-value? email)
             [:id "An email address is required."])
  (vali/rule (vali/is-email? email)
             [:id "A valid email is required."])
  (vali/rule (not (db/username-exists? email))
             [:id "This username already exists. Choose another."])
  (vali-password? pass confirm)
  (not (vali/errors? :id :pass :confirm)))

(defn admin-page [& [filter]]
  (layout/render "user/admin.html" {:users (db/get-all-users filter) :roles available-roles}))

(defn login-page [& [content]]
  (layout/render "user/login.html" content))

(defn account-created-page []
  (layout/render "user/account-created.html"))

(defn account-activated-page []
  (layout/render "user/account-activated.html"))

(defn signup-page [& [errormap]]
  (layout/render "user/signup.html" errormap))

(defn activate-account [id]
  (if (db/get-user-by-act-id id)
    (do (db/set-user-active id)
        (account-activated-page))
    (signup-page {:email-error "Please provide a correct activation id."})))

(defn changepassword-page [& [msgmap]]
  (layout/render "user/changepassword.html" msgmap))

(defn logout [] (sess/clear!) (resp/redirect "/"))

(defn login [request]
  (let [username (get-in request [:form-params "username"])
        password (get-in request [:form-params "password"])
        nexturl (get-in request [:form-params "nexturl"])]
    (if-let [user (db/get-user-by-email username)]
      (try
        (cond
          (= false (:is_active user)) (login-page {:error "Please activate your account first."})
          (= false (hashers/check password (get user :pass ""))) (login-page {:error "Please provide a correct password."})
          :else (do (sess/put! :role (:role user)) (sess/put! :identity username)
                    (resp/redirect (or nexturl "/"))))
        (catch Exception e (timb/error e "Something messed up while logging in user: " username " with error: " e)
                           (login-page {:error "Some error occured."})))
      (login-page {:error "Please provide a correct username."}))))

(defn add-user [email password confirm sendmail?]
  (if (valid-register? email password confirm)
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
  (let [user (db/get-user-by-email (uservice/get-logged-in-username))]
    (vali-password? password confirm oldpassword (:pass user))
    (if (not (vali/errors? :oldpass :pass :confirm))
      (do (db/change-password (:email user) (hashers/encrypt password))
          (changepassword-page {:success "Password changed successfully."}))
      (let [old-error (vali/on-error :oldpass first)
            pass-error (vali/on-error :pass first)
            confirm-error (vali/on-error :confirm first)]
        (changepassword-page {:pass-error pass-error :confirm-error confirm-error :old-error old-error})))))

(defroutes user-routes
           (GET "/admin/users" [filter] (admin-page filter))
           (GET "/user/login" [next] (login-page {:nexturl next}))
           (POST "/user/login" req (login req))
           (GET "/user/logout" [] (logout))
           (GET "/user/signup" [] (signup-page))
           (GET "/user/accountcreated" [] (account-created-page))
           (GET "/user/activate/:id" [id] (activate-account id))
           (POST "/user/signup" [email password confirm] (add-user email password confirm true))
           (GET "/user/changepassword" [] (changepassword-page))
           (POST "/user/changepassword" [oldpassword password confirm] (changepassword oldpassword password confirm)))

