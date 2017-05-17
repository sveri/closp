(ns {{ns}}.crudify.edit
  (:require [{{ns}}.views.base :as v]
            [{{ns}}.crudify.forms :as fo]
            [{{ns}}.crudify.util :as u]
            [{{ns}}.db.entities :as ent]
            [ring.util.response :as resp]
            [{{ns}}.crudify.validation :as valid]))



(defn edit-get [{:keys [localize] :as req} action db entities entity-type]
  (let [entity (get entities entity-type)
        table-name (:table-name entity)
        fields (:fields entity)
        db-data (ent/get-by-id db entity (u/get-id-from-params req))]
    (v/render
      "" req
      [:div.container-fluid.block-center
       [:div {:class "row"}
        [:div {:class "form-group col-md-6"}
         [:form {:action (format "/crud/%s/edit" table-name), :method "POST", :role "form"}
          (v/af-token)
          [:input {:type "hidden" :value (:id db-data) :name "id"}]

          [:h3 (localize [:generic/edit] [(u/prepare-str-for-ui table-name)])]

          (for [field fields]
            (fo/get-from-group-for-field field entities db db-data))


          [:div {:class "form-group"}
           [:input.btn.btn-primary {:type "submit", :value (localize [:generic/save])}]]]]
        [:div {:class "col-md-6"}]]])))


(defn edit-post [{:keys [localize form-params] :as req} action db entities entity-type]
  (let [table-name (:table-name (get entities entity-type))
        entity (get entities entity-type)]
    (try
      (valid/validate entity localize req)
      (ent/edit db entity req)
      (v/flash-result (localize [:generic/saved] [table-name]) "alert-success")
      (resp/redirect (u/get-link "/crud/%s/index" entity))
      (catch Exception e
        (u/log-error-and-redirect e (format "/crud/%s/edit?id=%s" table-name (u/get-id-from-form-params req)))))))