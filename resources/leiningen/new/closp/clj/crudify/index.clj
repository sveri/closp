(ns {{ns}}.crudify.index
  (:require [{{ns}}.views.base :as v]
            [{{ns}}.crudify.util :as u]
            [hiccup.core :as h]
            [{{ns}}.db.entities :as ent]))


(defmulti get-table-display-value-for-field (fn [table-row field db entities] (:type field)))

(defmethod get-table-display-value-for-field :fk [table-row field db entities]
  (let [field-id (get table-row (keyword (:name field)))
        fk-entity (get entities (:fk-ref field))
        sql-result (ent/get-by-id db fk-entity field-id)]
    (-> sql-result (get (:fk-ref-column field)))))


(defmethod get-table-display-value-for-field :default [table-row field db entities]
  (get-in table-row [(keyword (:name field))]))



(defn index-get [{:keys [localize] :as req} action db entities entity-type]
  (let [entity (get entities entity-type)
        fields (:fields entity)
        table-name (:table-name entity)
        table-rows (ent/get-all db entity)]
    (v/render
      "" req
      [:div
       [:a.btn.btn-primary {:href (format "/crud/%s/create" table-name)} (localize [:generic/create] [(u/prepare-str-for-ui table-name)])]
       [:br]
       [:br]
       [:br]
       [:table.table
        [:tr
         (for [field fields]
           [:th (u/prepare-str-for-ui (h/h (:name field)))])
         [:th]
         [:th]]

        (for [table-row table-rows]
          [:tr
           [:td.hidden [:input {:type "hidden" :name "id" :value (:id table-row)}]]
           (for [field fields]
             [:td (h/h (get-table-display-value-for-field table-row field db entities))])
           [:td [:a.btn.btn-primary {:href (format "/crud/%s/edit?id=%s" table-name (:id table-row))} (localize [:generic/edit])]]
           [:td [:a.btn.btn-danger {:href (format "/crud/%s/really-delete?id=%s" table-name (:id table-row))} (localize [:generic/delete])]]])]])))

