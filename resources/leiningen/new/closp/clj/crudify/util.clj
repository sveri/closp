(ns {{ns}}.crudify.util
  (:require [clojure.string :as str]
            [clojure.edn :as edn]
            [de.sveri.clojure.commons.log :as log]
            [{{ns}}.views.base :as v]
            [ring.util.response :as resp]))

(defn prepare-str-for-ui [s]
  (-> s
      (str/replace #"_id" "")
      str/capitalize))


(defn to-string [db-data entity]
  (let [to-string-field (first (:to-string entity))]
    (get db-data to-string-field)))



(defn get-link [s entity]
  (format s (:table-name entity)))

(defn get-id-from-params [req]
  (-> req :params (get :id) edn/read-string))

(defn get-id-from-form-params [req]
  (-> req :form-params (get "id") edn/read-string))



(defn log-error-and-redirect [e uri]
  (log/error e)
  (v/flash-result (.getMessage e) "alert-danger")
  (resp/redirect uri))
