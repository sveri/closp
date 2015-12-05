(ns {{ns}}.routes.cc
  (:require [compojure.core :refer [routes GET POST]]
            [schema.core :as s]
            [ring.util.response :refer [response status]]
            [{{ns}}.layout :as layout]
            [{{ns}}.service.cc :as serv-cc]
            [{{ns}}.closp-schema :as st])
  (:import (java.io File)
    (java.util UUID)) )

(defn cc-page []
  (layout/render "cc/index.html"))

(s/defn initial-data :- {:ex-entities st/existing-entities}
        [config :- s/Any]
        (let [files (->> (:closp-definitions config)
                         (File.)
                         (file-seq)
                         (filter #(.isFile %)))
              files-beautified (map (fn [f] {:name (.getName f) :content (slurp f)}) files)]
          (response {:ex-entities files-beautified :loaded true})))



(s/defn add-new-entity :- s/Any
        [description :- s/Str config :- s/Any]
        (try
          (spit (File. (str (:closp-definitions config) "/"
                            (get-in description [:name] (.toString (UUID/randomUUID))) ".edn"))
                (serv-cc/web-entity-desc->entity-desc description))
          (response {:ok "fine"})
          (catch Exception e
            (status 500 (response {:error "Something failed while saving the entity"}))))
        )

(defn cc-routes [config]
  (routes
    (GET "/admin/cc" [] (cc-page))
    (GET "/admin/cc/entities" [] (initial-data config))
    (POST "/admin/cc/entities" req (add-new-entity (:params req) config))))
