(ns {{ns}}.cc.core-middlepanel
  (:require [schema.core :as s]
            [com.rpl.specter :as spec]
            [ajax.core :as aj]
            [{{ns}}.helper :as h]
            [{{ns}}.closp-schema :as schem]
            [{{ns}}.closp-schema-helper :as schem-helper]
            [{{ns}}.cc.common :as com]))

(defn wrap-with-form [label-name form-field]
  [:div
   [:div.form-group
    [:label label-name]
    form-field]])

(defn spec-transform-column [new-f col-name]
  (swap! com/state assoc :new-entity
         (:new-entity (spec/transform [:new-entity :columns spec/ALL #(= (first %) col-name)]
                                      new-f
                                      @com/state))))

(defn change-column-name [e old-col-name]
  (spec-transform-column #(assoc % 0 (keyword (-> e .-target .-value))) old-col-name))

(defn change-column-type [e col-name]
  (let [col-type (-> e .-target .-value)]
    (cond
      (= "varchar" col-type) (spec-transform-column #(assoc % 1 [:varchar 100]) col-name)
      :else (spec-transform-column #(assoc % 1 (keyword col-type)) col-name))))

(s/defn ->disabled? :- s/Bool [col-name] (= :empty-name col-name))

(s/defn ->field-description :- nil
        [state column :- (s/conditional #(= :empty-name (first %)) schem/default-cljs-column
                                        :else schem/cc-table-column)]
        (let [field-type (name (schem-helper/get-type-of-column column))
              column-name (first column)]
          [:div
           [:div.row
            [:div.col-md-4
             (wrap-with-form
               "Field Name"
               [:input.form-control
                {:on-change   #(change-column-name % (if (= :empty-name column-name) :empty-name column-name))
                 :placeholder "Field Name"
                 :value       (if (= :empty-name column-name) "" (name column-name))}])]
            [:div.col-md-4
             [:div.row
              [:div.col-md-8
               (wrap-with-form "Field Type"
                               [:select.form-control
                                {:on-change #(change-column-type % column-name)
                                 :value     field-type
                                 :disabled (->disabled? column-name)}
                                [:option {:value "varchar"} "Varchar"]
                                [:option {:value "text"} "Text"]
                                [:option {:value "boolean"} "Boolean"]])]
              (when (= "varchar" field-type)
                [:div.col-md-4
                 (wrap-with-form "Length"
                                 [:input.form-control
                                  {:value     (second (second column))
                                   :on-change (fn [e] (spec-transform-column
                                                        #(assoc % 1 [:varchar (js/parseInt (-> e .-target .-value))])
                                                        column-name))
                                   :disabled (->disabled? column-name)}])])]]
            [:div.col-md-4
             (wrap-with-form "Nullable"
                             [:input.form-control
                              {:type      "checkbox"
                               :checked   (last column)
                               :on-change (fn [e] (spec-transform-column
                                                    #(assoc % 3 (-> e .-target .-checked))
                                                    column-name))
                               :disabled (->disabled? column-name)}])]]]))

(defn post-new-entity [_ state]
  (aj/POST "/admin/cc/entities"
           {:params        (:new-entity @state)

            :headers       {:X-CSRF-Token (h/get-value "__anti-forgery-token")}
            :handler       (fn [e]
                             (swap! state update-in [:ex-entities] conj
                                    (:added-entity e)))
            :error-handler (fn [e] (println "some error occured: " e))}))

(defn crudify-entity [state]
  (aj/POST "/admin/cc/entities/crudify"
           {:params        (:new-entity @state)
            :headers       {:X-CSRF-Token (h/get-value "__anti-forgery-token")}
            :handler       (fn [e] (println "succ"))
            :error-handler (fn [e] (println "some error occured: " e))}))

(defn has-not-last-col-name? [state]
  (->disabled? (first (last (get-in @state [:new-entity :columns])))))

(defn save-enabled? [state]
  (not (and (not (has-not-last-col-name? state)) (not-empty (get-in @state [:new-entity :name])))))

(defn middle-panel [state]
  [:div
   [:button.btn.btn-primary
    {:on-click #(crudify-entity state)
     :disabled (save-enabled? state)}
    "Crudify"]
   [:hr]
   (wrap-with-form "Entity Name"
                   [:input.form-control
                    {:placeholder "Entity Name"
                     :required    true
                     :on-change   #(swap! state assoc-in [:new-entity :name] (-> % .-target .-value))
                     :value       (get-in @state [:new-entity :name])}])
   [:hr]
   (let [columns (get-in @state [:new-entity :columns])]
     (for [field-nr (range 0 (count columns))]
       ^{:key field-nr} [->field-description state (nth columns field-nr)]))
   [:hr]
   [:button.btn.btn-default
    {:on-click #(swap! state update-in [:new-entity :columns] conj com/default-column)
     :disabled (has-not-last-col-name? state)} "Add Field"]
   [:button.btn.btn-primary.pull-right
    {:on-click #(post-new-entity % state)
     :disabled (save-enabled? state)} "Save"]])
