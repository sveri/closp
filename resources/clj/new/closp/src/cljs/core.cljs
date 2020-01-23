(ns {{namespace}}.core
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [{{namespace}}.events :as events]
            [{{namespace}}.config :as config]
            [{{namespace}}.views :as views]
            [{{namespace}}.routes :as routes]))




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
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (routes/app-routes)
  (mount-root))