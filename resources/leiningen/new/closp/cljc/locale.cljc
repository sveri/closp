(ns {{ns}}.cljc.locale
  (:require [taoensso.tempura :refer [tr]]
    #?(:cljs [{{ns}}.helper :as h])
    #?(:cljs [cljs.reader :as reader])))




(def local-dict
  {:de {:generic {:error                  "Es ist ein Fehler aufgetreten."
                  :restart                "Wiederholen"
                  :link                   "Link"
                  :should-contain-x-chars "Bitte mindestens %1 Zeichen eingeben"
                  :some_error             "Ein Fehler ist aufgetreten."
                  :deletion_canceled      "Löschen abgebrochen."
                  :email                  "E-Mail"
                  :logout                 "Ausloggen"
                  :diet                   "Ernährung"
                  :save                   "Speichern"
                  :search                 "Suchen"
                  :add                    "Hinzufügen"
                  :cancel                 "Abbrechen"
                  :please-wait            "Bitte warten"
                  :name                   "Name"
                  :create                 "%1 Anlegen"
                  :edit                   "%1 Bearbeiten"
                  :delete                 "Löschen"
                  :saved                  "Änderungen gespeichert"
                  :contact                "Kontakt"
                  :tos                    "Geschäftsbedingungen"
                  :cookies                "Cookies"}
        :user    {:email_invalid        "Bitte geben Sie eine gültige E-Mail Adresse an."
                  :login_failed         "Login fehlgeschlagen"
                  :password             "Passwort"
                  :password_confirm     "Passwort wiederholen"
                  :pass_min_length      "Das Passwort muss mindestens 5 Zeichen enthalten."
                  :pass_match           "Die eingegebenen Passwörter stimmen nicht überein."
                  :pass_correct         "Bitte geben Sie das richtige Passwort ein."
                  :pass_changed         "Passwort geändert."
                  :change_password      "Passwort ändern"
                  :wrong_cur_pass       "Das derzeitige Passwort ist falsch."
                  :username_exists      "Der Benutzername existiert bereits. Bitte geben Sie einen anderen an."
                  :username_wrong       "Bitte geben Sie einen gültigen Benutzernamen ein."
                  :deleted              "Benutzer erfolgreich gelöscht."
                  :updated              "Benuter %1 erfolgreich geändert."
                  :user_added           "Benutzer hinzugefügt."
                  :captcha_wrong        "Bitte geben Sie das Captcha richtig ein."
                  :email_failed         "Etwas schlug fehl beim verschicken der Mail. Bitte wenden Sie sich an den Support."
                  :signup               "Anmelden"
                  :signin               "Einloggen"
                  :username             "Benutzername"
                  :role                 "Rolle"
                  :current_password     "Aktuelles Passwort"
                  :new_password         "Neues Passwort"
                  :new_password_confirm "Neues Passwort bestätigen"
                  :really_delete        "Möchten Sie %1 wirklich löschen?"
                  :register             "Registrieren"
                  :users                "Benutzer"}}


   :en {:generic {:error                  "An error occurred."
                  :restart                "Restart."
                  :link                   "Link"
                  :should-contain-x-chars "Should contain at least %1 characters"
                  :some_error             "Some error occured."
                  :deletion_canceled      "Deletion canceled."
                  :email                  "Email"
                  :logout                 "Logout"
                  :save                   "Save"
                  :search                 "Search"
                  :add                    "Add"
                  :cancel                 "Cancel"
                  :name                   "Name"
                  :create                 "Create %1"
                  :edit                   "Edit %1"
                  :delete                 "Delete"
                  :saved                  "Changes Saved"
                  :contact                "Contact"
                  :tos                    "Terms Of Service"
                  :cookies                "Cookies"}
        :user    {:email_invalid        "A valid email is required."
                  :login_failed         "Login Failure"
                  :password             "Password"
                  :password_confirm     "Confirm Password"
                  :pass_min_length      "Password must be at least 5 characters."
                  :pass_match           "Entered passwords do not match."
                  :pass_correct         "Please provide a correct password."
                  :pass_changed         "Password changed."
                  :change_password      "Change Password"
                  :wrong_cur_pass       "Current password was incorrect."
                  :username_exists      "This username already exists. Choose another."
                  :username_wrong       "Please provide a correct username."
                  :deleted              "User deleted successfully."
                  :updated              "User %1 updated successfully."
                  :user_added           "User added."
                  :captcha_wrong        "Please provide the correct captcha input."
                  :email_failed         "Something went wrong sending the email, please contact us."
                  :signup               "Sign Up"
                  :signin               "Sign In"
                  :username             "Username"
                  :role                 "Role"
                  :current_password     "Current Password"
                  :new_password         "New Password"
                  :new_password_confirm "Confirm New Password"
                  :really_delete        "Do you really want to delete: %1?"
                  :register             "Register"
                  :users                "Users"}}})





#?(:cljs (def localize (partial tr
                                {:default-locale :en
                                 :dict           local-dict}
                                (reader/read-string (h/get-value "languages")))))


; need to create a partial in .clj with locales added
#?(:clj (def localize (partial tr
                               {:default-locale :en
                                :dict           local-dict}
                               ["en"])))
