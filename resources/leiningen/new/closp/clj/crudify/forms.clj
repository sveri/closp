(ns {{ns}}.crudify.forms
  (:require [{{ns}}.crudify.util :as u]
            [hiccup.core :as h]
            [{{ns}}.db.entities :as ent]))






(defmulti get-form-for-type (fn [field entities db & [db-data]] (:type field)))


(defmethod get-form-for-type :string [field entities db & [db-data]]
  [:input.form-control (merge {:name (:name field), :type "text" :placeholder (u/prepare-str-for-ui (:name field))}
                              (when db-data {:value (get db-data (keyword (:name field)))}))])


(defmethod get-form-for-type :fk [field entities db & [db-data]]
  (let [fk-entity (get entities (:fk-ref field))
        fk-data-rows (ent/get-all db fk-entity)]
    [:div
     [:select.form-control {:name (:name field)}
      (for [fk-data-row fk-data-rows]
        [:option (merge {:value (h/h (:id fk-data-row))}
                        (when (and db-data (= (:id fk-data-row) (get db-data (keyword (get field :name))))) {:selected "selected"}))
         (h/h (get fk-data-row (:fk-ref-column field)))])]]))




(defn get-from-group-for-field [field entites db & [db-data]]
  [:div.form-group
   [:label (u/prepare-str-for-ui (:name field))]
   (get-form-for-type field entites db db-data)])