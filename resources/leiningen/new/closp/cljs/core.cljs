(ns {{ns}}.core
  (:require [re-frame.core :as re-frame]
            [{{ns}}.events :as events]
            [{{ns}}.config :as config]
            [{{ns}}.views :as views]
            [{{ns}}.routes :as routes]))




(defn dev-setup []
  (when config/debug?
    (set! *warn-on-infer* true)
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (routes/app-routes)
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root))