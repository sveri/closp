(ns {{ns}}.crudify.delete
  (:require [ring.util.response :as resp]
            [{{ns}}.views.base :as v]
            [{{ns}}.crudify.util :as u]
            [{{ns}}.db.entities :as ent]
            [clojure.edn :as edn]
            [hiccup.core :as h]))

(defn really-delete-get [{:keys [localize] :as req} action db entities entity-type]
  (let [entity (get entities entity-type)
        id (edn/read-string  (get-in req [:params :id]))
        db-data (ent/get-by-id db entity id)]
    (v/render
     (localize [:user/really_delete]) req
     [:div
      [:p (localize [:user/really_delete] [(h/h (u/to-string db-data entity))])
       [:form {:method "post", :action (u/get-link "/crud/%s/delete" entity)}
        (v/af-token)
        [:input {:type "hidden", :name "id", :value id}]
        [:span {:field "submit"}
         [:input.btn.btn-primary {:type "submit", :name "delete_cancel", :value (localize [:generic/cancel])}]]
        [:span {:field "submit" :style "margin-left: 40px"}
         [:input.btn.btn-danger {:type "submit", :name "delete_cancel", :value (localize [:generic/delete])}]]]]])))


(defn delete-post [{:keys [form-params localize] :as req} action db entities entity-type]
  (let [table-name (:table-name (get entities entity-type))]
    (if (= (localize [:generic/cancel]) (get form-params "delete_cancel"))
      (resp/redirect (u/get-link "/crud/%s/index" (get entities entity-type)))
      (try
        (ent/delete db (get entities entity-type) ["id = ?" (u/get-id-from-form-params req)])
        (v/flash-result (localize [:crudify/deleted] [table-name]) "alert-success")
        (resp/redirect (u/get-link "/crud/%s/index" (get entities entity-type)))
        (catch Exception e
          (u/log-error-and-redirect e (format "/crud/%s/index" table-name)))))))
