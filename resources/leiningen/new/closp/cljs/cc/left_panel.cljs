(ns {{ns}}.cc.left-panel
  (:require [{{ns}}.cc.common :as com]))

(defn edit-entity [e]
  (println (-> e .-target .-title)))

(defn render-ex-entities [ents]
  [:div
   [:h3 "Existing Entities"]
   [:hr]
   [:ul
    (for [e ents]
      ^{:key e}
      [:li {:title (:content e) :style {:cursor "pointer"} :on-click edit-entity} (:name e)])]])

(defn left-panel [state]
  (let [ex-entities (:ex-entities @state)]
    [:div
     [:button.btn.btn-primary {:on-click #(swap! state assoc :new-entity com/new-entity-definition)}
      "Create new Entity"]
     [render-ex-entities ex-entities]]))
