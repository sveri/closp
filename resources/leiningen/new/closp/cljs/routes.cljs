(ns {{ns}}.routes
  (:require [{{ns}}.common :as common]
            [{{ns}}.helper :refer [>evt]]
            [bidi.bidi :as bidi]
            [pushy.core :as pushy]
            [re-frame.core :as rf]))

(def routes ["/" {""        :home
                  "about"   :about
                  "contact" :contact
                  "tos"     :tos
                  "cookies" :cookies
                  "user/"   {"login"          :login
                             "signup"         :signup
                             "changepassword" :changepassword}}])
;
;;parse-url function uses bidi/match-route to turn a URL into a ds
(defn- parse-url [url]
       (bidi/match-route routes url))

;;dispatch-route is called with that structure:
(defn dispatch-route [matched-route]
      (let [panel-name (keyword (str (name (:handler matched-route))))]
           (>evt [::common/set-active-panel panel-name])))


; The app-routes function that used to define functions is replaced by one that sets up pushy:
(def history (pushy/pushy dispatch-route parse-url))
(defn app-routes []
      (pushy/start! history))


(def url-for (partial bidi/path-for routes))

(rf/reg-fx :navigate-to #(do (pushy/set-token! {{ns}}.routes/history (url-for %))
                             (dispatch-route {:handler %})))
(def url-for (partial bidi/path-for routes))