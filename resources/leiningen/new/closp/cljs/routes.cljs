(ns {{ns}}.routes
  (:require [{{ns}}.events :as events]
            [re-frame.core :as re-frame]
            [bidi.bidi :as bidi]
            [pushy.core :as pushy]))



(def routes ["/" {"" :home
                  "about" :about}])

;;parse-url function uses bidi/match-route to turn a URL into a ds
(defn- parse-url [url]
       (bidi/match-route routes url))

;;dispatch-route is called with that structure:
(defn- dispatch-route [matched-route]
       (let [panel-name (keyword (str (name (:handler matched-route)) "-panel"))]
            (re-frame/dispatch [::events/set-active-panel panel-name])))


;; The app-routes function that used to define functions is replaced by one that sets up pushy:
(defn app-routes []
      (pushy/start! (pushy/pushy dispatch-route parse-url)))

(def url-for (partial bidi/path-for routes))