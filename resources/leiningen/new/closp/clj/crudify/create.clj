(ns {{ns}}.crudify.create
  (:require [ring.util.response :as resp]
            [{{ns}}.views.base :as v]
            [{{ns}}.crudify.validation :as valid]
            [{{ns}}.crudify.util :as u]
            [hiccup.core :as h]
            [{{ns}}.db.entities :as ent]
            [{{ns}}.crudify.forms :as fo]
            [de.sveri.clojure.commons.log :as log]
            [clojure.string :as str])
  (:import (java.io StringWriter PrintWriter)))



(defn create-get [{:keys [localize] :as req} action db entities entity-type]
  (let [entity (get entities entity-type)
        table-name (:table-name entity)
        fields (:fields entity)]
    (v/render
      "" req
      [:div.container-fluid.block-center
       [:div {:class "row"}
        [:div {:class "form-group col-md-6"}
         [:form {:action (format "/crud/%s/create" table-name), :method "POST", :role "form"}
          (v/af-token)

          [:h3 (localize [:generic/create] [(u/prepare-str-for-ui table-name)])]

          (for [field fields]
            (fo/get-from-group-for-field field entities db))

          [:div {:class "form-group"}
           [:input.btn.btn-primary {:type "submit", :value (localize [:generic/create])}]]]]
        [:div {:class "col-md-6"}]]])))



(defn create-post [{:keys [localize] :as req} action db entities entity-type]
  (let [entity (get entities entity-type)
        table-name (:table-name entity)]
    (try
      (valid/validate entity localize req)
      (ent/create db entity req)
      (v/flash-result (localize [:crudify/created] [table-name]) "alert-success")
      (resp/redirect (format "/crud/%s/index" table-name))
      (catch Exception e
        (u/log-error-and-redirect e (format "/crud/%s/create" table-name))))))

