(ns {{ns}}.components.locale
  (:require [com.stuartsierra.component :as component]))

(def tconfig
  {:fallback-locale :en
   :dictionary
                    {:en   {:user {:email_invalid "Invalid email address."
                                      :content "Time to start building your site."}}
                     }})

(defrecord Locale []
  component/Lifecycle
  (start [component](assoc component :tconfig tconfig))
  (stop [component] (dissoc component :tconfig)))

(defn new-locale []
  (map->Locale {}))
