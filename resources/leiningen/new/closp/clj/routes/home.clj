(ns {{ns}}.routes.home
  (:require [compojure.core :refer [defroutes ANY]]
            [{{ns}}.views.base :as v]))

(defn home-page [req]
  (v/render {{name}} req))

(defroutes home-routes
           (ANY "*" req (home-page req)))
