(ns {{ns}}.routes.cc
  (:require [compojure.core :refer [routes GET POST]]
            [foo.bar.layout :as layout]
            ))

(defn cc-page []
  (layout/render "cc/index.html"))

(defn cc-routes []
  (routes
    (GET "/admin/cc" req (cc-page))))
