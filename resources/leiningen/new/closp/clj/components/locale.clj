(ns {{ns}}.components.locale
  (:require [com.stuartsierra.component :as component]))

(def tconfig
  {:fallback-locale :en
   :dictionary      {:en
                     {:generic
                      {:some_error        "Some error occured."
                       :deletion_canceled "Deletion canceled."}
                      :user
                      {:email_invalid           "A valid email is required."
                       :pass_min_length         "Password must be at least 5 characters."
                       :pass_match              "Entered passwords do not match."
                       :pass_correct            "Please provide a correct password."
                       :pass_changed            "Password changed."
                       :wrong_cur_pass          "Current password was incorrect."
                       :username_exists         "This username already exists. Choose another."
                       :username_wrong          "Please provide a correct username."
                       :deleted                 "User deleted successfully."
                       :updated                 "User %s updated successfully."
                       :user_added              "User added."
                       :captcha_wrong           "Please provide the correct captcha input."
                       :activationid_wrong      "Please provide a correct activation id."
                       :activate_account        "Please activate your account first."
                       :account_created         "Account creation successful. You will receive an email with an activation link. Please click on it to activate your account."
                       :account_activated       "Account activated, you can login now. Redirecting in five seconds..."
                       :email_failed            "Something went wrong sending the email, please contact us."
                       :signup_title            "Signup"
                       :account_created_title   "Account Created"
                       :account_activated_title "Account Activated"
                       }
                      :admin
                      {:title "User Overview"
                       }}}})

(defrecord Locale []
  component/Lifecycle
  (start [component](assoc component :tconfig tconfig))
  (stop [component] (dissoc component :tconfig)))

(defn new-locale []
  (map->Locale {}))
