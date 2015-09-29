(ns {{ns}}.routes.cc
  (:require [compojure.core :refer [routes GET POST]]
            [{{ns}}.layout :as layout]
            [ring.util.response :refer [response]]))

(defn cc-page []
  (layout/render "cc/index.html"))

(defn initial-data [config]
  (let [files (->> (:closp-definitions config)
                   (File.)
                   (file-seq)
                   (filter #(.isFile %)))
        files-beautified (map (fn [f] {:name (.getName f) :content (slurp f)}) files)]
    (response {:ex-entities files-beautified :loaded true})))

(defn cc-routes [config]
  (routes
    (GET "/admin/cc" [] (cc-page))
    (GET "/admin/cc/initial" [] (initial-data config))))
