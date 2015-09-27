(ns {{ns}}.cc.core
  (:require [reagent.core :as reagent :refer [atom]]
            [{{ns}}.helper :as h]
            [ajax.core :refer [GET]]))


;{:ex-entities []
; :new-entity {:cur-id Int :name String :fields [{:name "" :type "" :nullable false :id 1}]}}
(def state (atom {}))


(def new-entity-definition {:cur-id 1 :name "" :fields {1 {:name "" :type "" :nullable false :id 1}}})
;(def new-entity-definition {:cur-id 1 :name "" :fields [{:name "" :type "" :nullable false :id 1}]})

(defn initialized? []
      (nil? (get @state :ex-entities)))

(defn render-ex-entities [ents]
      [:div
       [:h3 "Existing Entities"]
       [:ul
        (for [e ents]
             ^{:key e} [:li {:title (:content e)} (:name e)])]])

(defn left-panel []
      [:div
       [:button.btn.btn-primary {:on-click
                                 #(swap! state assoc :new-entity new-entity-definition)}
        "Create new Entity"]
       (let [ex-entities (:ex-entities @state)]
            [render-ex-entities ex-entities])])


(defn wrap-with-form [label-name form-field]
      [:div
       [:div.form-group
        [:label label-name]
        form-field]])


;{:fields [{:name "n1" :type "Varchar" :nullable false}]}
(defn ->field-description [field-id]
      (let [field (get-in @state [:new-entity :fields field-id])]
           [:div
            ;[:input {:type "hidden" :value id}]
            [:div.row
             [:div.col-md-4 (wrap-with-form "Field Name"
                                            [:input.form-control
                                             {:on-change   #(println "changed")
                                              :placeholder "Field Name" :value (:name field)}])]
             [:div.col-md-4 (wrap-with-form "Field Type" [:select.form-control
                                                          [:option "Varchar"]
                                                          [:option "Text"]
                                                          [:option "Boolean"]])]
             [:div.col-md-4 (wrap-with-form "Nullable" [:input.form-control {:type "checkbox"}])]]]))

(defn middle-panel []
      (let [new-fields (get-in @state [:new-entity :fields])
            fields-count (get-in @state [:new-entity :cur-id])]
           [:div
            (wrap-with-form "Entity Name"
                            [:input.form-control {:placeholder "Entity Name"
                                                  :on-change   #(swap! state assoc-in [:new-entity :name]
                                                                       (-> % .-target .-value))
                                                  :value       (get-in @state [:new-entity :name])}])
            [:hr]
            (for [field-nr (range 1 (+ 1 fields-count))]
                 ^{:key field-nr}
                 [->field-description field-nr])
            [:hr]
            [:button.btn.btn-default
             {:on-click #(do
                          (swap! state update-in [:new-entity :fields] conj
                                 {:name "" :type "" :nullable false :id (+ 1 (get-in @state [:new-entity :cur-id]))})
                          (swap! state update-in [:new-entity :cur-id] + 1))}
             "Add Field"]
            [:button.btn.btn-primary.pull-right "Save"]]))

(defn page []
      (let [loaded (:ex @state)]
           (if (initialized?)
             [:div.row
              [:div.col-md-3 [left-panel]]
              [:div.col-md-8 [middle-panel]]]
             [:div "Loading"])))

(defn init-state []
      (swap! state assoc :new-entity new-entity-definition)
      (GET "/admin/cc/initial" {:handler       #(swap! state assoc :ex-entities (:ex-entities %))
                                :error-handler #(println "some error occured: " %)}))

(defn ^:export main []
      (init-state)
      (reagent/render-component (fn [] [page]) (h/get-elem "app")))
