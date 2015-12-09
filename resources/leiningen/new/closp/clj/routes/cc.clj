(ns {{ns}}.routes.cc
  (:require [compojure.core :refer [routes GET POST]]
            [schema.core :as s]
            [ring.util.response :refer [response status]]
            [clojure.pprint :as pp]
            [taoensso.timbre :as timb]
            [{{ns}}.layout :as layout]
            [{{ns}}.service.cc :as serv-cc]
            [{{ns}}.closp-schema :as c-schem])
  (:import (java.io File)
    (java.util UUID)))

(defn cc-page []
  (layout/render "cc/index.html"))

(s/defn initial-data :-
        (c-schem/wrap-with-response {:ex-entities c-schem/existing-entities})
        [config :- s/Any]
        (let [files (->> (:closp-definitions config)
                         (File.)
                         (file-seq)
                         (filter #(.isFile %)))
              files-beautified (map (fn [f] {:name (.getName f) :content (slurp f)}) files)]
          (response {:ex-entities files-beautified})))

(s/defn write-cc-entity-to-file :- s/Str
        [new-entity :- c-schem/cljs-new-entity-definition config :- s/Any]
        (let [cc-entity (with-out-str (pp/pprint
                                        (serv-cc/cljs-new-entity->cc-entity new-entity)))]
          (spit
            (File. (str (:closp-definitions config) "/"
                        (get-in new-entity [:name] (.toString (UUID/randomUUID))) ".edn"))
            cc-entity)
          cc-entity))

(s/defn add-new-entity :-
        (c-schem/wrap-with-response {:ok s/Str :added-entity c-schem/existing-entity})
        [new-entity :- c-schem/cljs-new-entity-definition config :- s/Any]
        (try
          (let [cc-entity (write-cc-entity-to-file new-entity config)]
            (response {:ok "fine" :added-entity {:name (:name new-entity) :content cc-entity}}))
          (catch Exception e
            (do (timb/error e)
                (status 500 (response {:error "Something failed while saving the entity"}))))))

(defn cc-routes [config]
  (routes
    (GET "/admin/cc" [] (cc-page))
    (GET "/admin/cc/entities" [] (initial-data config))
    (POST "/admin/cc/entities" req (add-new-entity (:params req) config))))