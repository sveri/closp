(ns {{ns}}.cc.core
  (:require [reagent.core :as reagent :refer [atom]]
            [ajax.core :refer [GET POST]]
            [schema.core :as s :include-macros true]
            [com.rpl.specter :as spec]
            [{{ns}}.helper :as h]
            [{{ns}}.closp-schema :as schem]))

(def default-column
  (s/validate schem/default-cljs-column {:name "" :type "varchar" :length 100 :nullable false :id 1}))

(def new-entity-definition
  (s/validate schem/cljs-new-entity-definition
              {:cur-id  1 :name ""
               :columns [default-column]}))

(def state (atom {:ex-entities [] :new-entity new-entity-definition}))

(add-watch state :validate (fn [_ _ _ n] (s/validate schem/cljs-state n)))

(defn initialized? []
      (not (nil? (get @state :ex-entities))))

(defn render-ex-entities [ents]
      [:div
       [:h3 "Existing Entities"]
       [:hr]
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

(s/defn update-new-entity-column :- nil
        [id :- s/Num key :- s/Keyword val :- (s/->Either [s/Num s/Str s/Bool])]
        (reset! state
                (spec/transform [:new-entity :columns spec/ALL #(= (:id %) id)]
                                #(assoc % key val)
                                @state)))

(defn ->field-description [column-id]
      (let [field (first (filter #(= column-id (:id %)) (get-in @state [:new-entity :columns])))]
           [:div
            [:div.row
             [:div.col-md-4
              (wrap-with-form
                "Field Name"
                [:input.form-control
                 {:on-change   #(update-new-entity-column column-id :name (-> % .-target .-value))
                  :placeholder "Field Name" :value (:name field)}])]
             [:div.col-md-4
              [:div.row
               [:div.col-md-8
                (wrap-with-form "Field Type"
                                [:select.form-control
                                 {:on-change #(update-new-entity-column column-id :type (-> % .-target .-value))
                                  :value     (:type field)}
                                 [:option {:value "varchar"} "Varchar"]
                                 [:option {:value "text"} "Text"]
                                 [:option {:value "boolean"} "Boolean"]])]
               (when (= "varchar" (:type field))
                     [:div.col-md-4
                      (wrap-with-form "Length"
                                      [:input.form-control
                                       {:value     (:length field)
                                        :on-change #(update-new-entity-column column-id :length
                                                                              (js/parseInt (-> % .-target .-value)))}])])]]
             [:div.col-md-4
              (wrap-with-form "Nullable"
                              [:input.form-control
                               {:type      "checkbox"
                                :checked   (:nullable field)
                                :on-change #(update-new-entity-column column-id :nullable (-> % .-target .-checked))}])]]]))

(defn post-new-entity [_]
      (POST "/admin/cc/entities"
            {:params        (:new-entity @state)

             :headers       {:X-CSRF-Token (h/get-value "__anti-forgery-token")}
             :handler       (fn [e] (println (:added-entity e))
                                (println (:ex-entities @state))
                                (swap! state update-in [:ex-entities] conj
                                       (:added-entity e))
                                )
             :error-handler (fn [e] (println "some error occured: " e))}))

(defn middle-panel []
      (let [fields-count (get-in @state [:new-entity :cur-id])]
           [:div
            (wrap-with-form "Entity Name"
                            [:input.form-control
                             {:placeholder "Entity Name"
                              :required    true
                              :on-change   #(swap! state assoc-in [:new-entity :name] (-> % .-target .-value))
                              :value       (get-in @state [:new-entity :name])}])
            [:hr]
            (for [field-nr (range 1 (+ 1 fields-count))]
                 ^{:key field-nr}
                 [->field-description field-nr])
            [:hr]
            [:button.btn.btn-default
             {:on-click #(let [next-id (+ 1 (get-in @state [:new-entity :cur-id]))]
                              (swap! state assoc-in [:new-entity :cur-id] next-id)
                              (swap! state update-in [:new-entity :columns] conj
                                     (assoc default-column :id next-id)))}
             "Add Field"]
            [:button.btn.btn-primary.pull-right
             {:on-click post-new-entity} "Save"]]))

(defn page []
      (if (initialized?)
        [:div.row
         [:div.col-md-3 [left-panel (:existing-entities @state)]]
         [:div.col-md-8 [middle-panel]]]
        [:div "Loading"]))

(defn init-state []
      (GET "/admin/cc/entities"
           {:handler       #(swap! state assoc :ex-entities (:ex-entities %))
            :error-handler #(println "some error occured: " %)}))

(defn ^:export main []
      (init-state)
      (reagent/render-component (fn [] [page]) (h/get-elem "app")))