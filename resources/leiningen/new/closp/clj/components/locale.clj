(ns {{ns}}.components.locale
  (:require [com.stuartsierra.component :as component]))

(def tconfig
  {:fallback-locale :en
   :dictionary      {:en
                     {:user
                      {:email_invalid   "A valid email is required."
                       :pass_min_length "Password must be at least 5 characters."
                       :pass_match "Entered passwords do not match."
                       :wrong_cur_pass "Current password was incorrect."
                       :username_exists "This username already exists. Choose another."}}
                     }})

(defrecord Locale []
  component/Lifecycle
  (start [component](assoc component :tconfig tconfig))
  (stop [component] (dissoc component :tconfig)))

(defn new-locale []
  (map->Locale {}))
