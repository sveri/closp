(ns {{ns}}.cc.left-panel
  (:require [schema.core :as s]
            [cljs.pprint :as pp]
            [{{ns}} .closp-schema :as schem]
            [{{ns}} .cc.common :as com]))

(defn edit-entity [state name]
  (swap! state assoc :new-entity (first (filter #(= name (:name %)) (:ex-entities @state)))))

(s/defn render-ex-entities [state entities :- schem/cc-entity-definitons]
        (println (first entities))
        (println (:columns (first entities)))
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
