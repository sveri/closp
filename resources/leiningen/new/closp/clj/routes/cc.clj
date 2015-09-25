(ns {{ns}}.routes.cc
  (:require [compojure.core :refer [routes GET POST]]
            [foo.bar.layout :as layout]
            [ring.util.response :refer [response]]))

(defn cc-page []
  (layout/render "cc/index.html"))

(defn initial-data []
  (response {:ok "fooo" :loaded true}))

(defn cc-routes []
  (routes
    (GET "/admin/cc" [] (cc-page))
    (GET "/admin/cc/initial" [] (initial-data))))
