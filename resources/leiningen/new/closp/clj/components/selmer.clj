(ns {{ns}}.components.selmer
  (:require [com.stuartsierra.component :as component]
            [selmer.parser :as sp]))

(defn localize-tag []
  (sp/add-tag!
   :localize
   (fn [args context-map]
     ((:localize-fn context-map) [(keyword (first args))] (vec (rest args))))))

(defrecord Selmer [template-caching?]
  component/Lifecycle
  (start [component]
    (localize-tag)
    (if template-caching?
      (sp/cache-on!)
      (sp/cache-off!))
    component)
  (stop [component] component))

(defn new-selmer [template-caching?]
  (->Selmer template-caching?))
