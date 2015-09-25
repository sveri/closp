(ns {{ns}}.cc.core
  (:require [reagent.core :as reagent :refer [atom]]
            [foo.bar.helper :as h]
            [ajax.core :refer [GET]]))

(def state (atom {}))

(defn render-ex-entities [ents]
  [:div
   [:h3 "Existing Entities"]
   [:ul
    (for [e ents]
      ^{:key e} [:li {:title (:content e)} (:name e)])]])

(defn entry-point []
  (let [loaded (:loaded @state)]
    (if loaded
      [:div
       [:button.btn.btn-primary "Create new Entity"]
       (let [ex-entities (:ex-entities @state)]
         [render-ex-entities ex-entities])]
      [:div "Loading"])))

(defn init-state []
  (GET "/admin/cc/initial" {:handler       #(reset! state %)
                            :error-handler #(println "some error occured: " %)}))

(defn ^:export main []
  (init-state)
  (reagent/render-component (fn [] [entry-point]) (h/get-elem "app")))
