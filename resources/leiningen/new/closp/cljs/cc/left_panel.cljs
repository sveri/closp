(ns {{ns}}.cc.left-panel
  (:require [{{ns}}.cc.common :as com]
            [schema.core :as s]
            [{{ns}}.closp-schema :as schem]
            [cljs.pprint :as pp]))

(defn edit-entity [state name]
  (swap! state assoc :new-entity (first (filter #(= name (:name %)) (:ex-entities @state)))))

(s/defn render-ex-entities [state entities :- schem/cc-entity-definitons]
        [:div
         [:h3 "Existing Entities"]
         [:hr]
         [:ul
          (for [e entities]
            (let [name (:name e)]
              ^{:key (:name e)}
              [:li {:title (with-out-str (pp/pprint e)) :style {:cursor "pointer"} :on-click #(edit-entity state name)}
               name]))]])

(defn left-panel [state]
  (let [ex-entities (:ex-entities @state)]
    [:div
     [:button.btn.btn-primary {:on-click #(swap! state assoc :new-entity com/new-entity-definition)} "Create new Entity"]
     [render-ex-entities state ex-entities]]))
