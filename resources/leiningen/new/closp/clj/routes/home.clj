(ns {{ns}}.routes.home
  (:require [compojure.core :refer :all]
            [{{ns}}.layout :as layout]
            [{{ns}}.util :as util]))

(defn home-page []
  (layout/render
    "app.html"
    ;{:content (util/md->html "/md/docs.md")}
    )
  )

(defn about-page []
  (layout/render "about.html"))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/about" [] (about-page)))
