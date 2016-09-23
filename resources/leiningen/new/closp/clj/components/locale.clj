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
                       :email_failed            "Something went wrong sending the email, please contact us."
                       :signup_title            "Signup"}

                      :admin
                      {:title "User Overview"}}}})


(defrecord Locale []
  component/Lifecycle
  (start [component](assoc component :tconfig tconfig))
  (stop [component] component))

(defn new-locale []
  (map->Locale {}))
