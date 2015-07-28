(ns {{ns}}.components.locale
  (:require [com.stuartsierra.component :as component]))

(def tconfig
  {:fallback-locale :en-US
   :dictionary
                    {:en-US   {:user {:email_invalid "Invalid email address."
                                      :content "Time to start building your site."}
                               :missing  "<Missing translation: [%1$s %2$s %3$s]>"}
                     :fr-FR {:page {:title "Voici un titre"
                                    :content "Il est temps de commencer votre site."}}
                     }})

(defrecord Locale []
  component/Lifecycle
  (start [component](assoc component :tconfig tconfig))
  (stop [component] (dissoc component :tconfig)))

(defn new-locale []
  (map->Locale {}))
