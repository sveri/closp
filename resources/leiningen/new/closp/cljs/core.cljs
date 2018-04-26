(ns {{ns}}.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as rf]
            [re-pressed.core :as rp]
            [ajax.interceptors :as ai]
            [ajax.core :as ajax]
            [de.sveri.estar.effects]
            [{{ns}}.helper :refer [>evt-sync]]
            [{{ns}}.events :as events]
            [{{ns}}.config :as config]
            [{{ns}}.views :as views]
            [{{ns}}.routes :as routes]
            [{{ns}}.user.helper :as uh]))



(def jwt-interceptor
  (ai/to-interceptor {:name    "JWT Token Interceptor"
                      :request #(-> %
                                    (assoc-in [:headers "Authorization"] (str "Token " (uh/get-jwt-token-from-localstorage)))
                                    (assoc :format (ajax/transit-request-format)))}))

(defn dev-setup []
      (when config/debug?
            (set! *warn-on-infer* true)
            (enable-console-print!)
            (println "dev mode")))

(defn mount-root []
      (rf/clear-subscription-cache!)
      (reagent/render [views/main-panel] (.getElementById js/document "app")))

(defn ^:export init []
      (dev-setup)
      (swap! ajax/default-interceptors (partial cons jwt-interceptor))
      (>evt-sync [::rp/add-keyboard-event-listener "keydown"])
      (>evt-sync [::events/initialize-db])
      (routes/app-routes)
      (mount-root))
