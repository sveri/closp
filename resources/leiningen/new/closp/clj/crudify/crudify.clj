(ns {{ns}}.crudify.crudify
  (:require [{{ns}}.crudify.create :as c]
            [{{ns}}.crudify.delete :as d]
            [{{ns}}.crudify.edit :as e]
            [{{ns}}.crudify.index :as i]))



(defmulti crudify (fn [req action db entities entity-type] [action (:request-method req)]))

(defmethod crudify ["index" :get] [req action db entities entity-type]
  (i/index-get req action db entities entity-type))


(defmethod crudify ["create" :get] [req action db entities entity-type]
  (c/create-get req action db entities entity-type))


(defmethod crudify ["create" :post] [req action db entities entity-type]
  (c/create-post req action db entities entity-type))


(defmethod crudify ["edit" :get] [req action db entities entity-type]
  (e/edit-get req action db entities entity-type))

(defmethod crudify ["edit" :post] [req action db entities entity-type]
  (e/edit-post req action db entities entity-type))


(defmethod crudify ["really-delete" :get] [req action db entities entity-type]
  (d/really-delete-get req action db entities entity-type))

(defmethod crudify ["delete" :post] [req action db entities entity-type]
  (d/delete-post req action db entities entity-type))
