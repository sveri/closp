(ns {{ns}}.ajax
   (:require [reagent.core :as reagent :refer [atom]]
             [{{ns}}.helper :as h]
             [ajax.core :refer [GET]]))

(def state (atom {}))

(defn entry-point []
  (let [loaded (:loaded @state)]
       (if loaded
         [:div "loaded"]
         [:div "Loading"])))

(defn init-state []
  (GET "/ajax/page/init" {:handler       #(do
                                          (println "succ: " %)
                                          (reset! state %))
                          :error-handler #(println "some error occured: " %)}))

(defn ^:export main []
  (init-state)
  (reagent/render-component (fn [] [entry-point]) (h/get-elem "app")))

