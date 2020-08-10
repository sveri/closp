(ns {{namespace}}.views.home
  (:require [{{namespace}}.views.util :as vu]))



(defn home-page [req]
  (vu/render-content-with req "ClospTemplate" [:h2 "Closp Template"]))

(defn contact-page [{:keys [localize] :as req}]
  (vu/render-content-with
    req
    (localize [:contact/contact])
    [:div
     [:div.container
      [:h2 (localize [:contact/contact])]
      [:span (localize [:contact/content_belongs_to])]
      [:br]
      [:span (localize [:contact/contact])]]]))

(defn tos-page [{:keys [localize] :as req}]
  (vu/render-content-with
    req
    (localize [:generic/tos])
    [:div
     [:div.container
      [:div.row
       [:div.span6
        [:h1 "TOS"]]]]]))

(defn cookies-page [{:keys [localize] :as req}]
  (vu/render-content-with
    req
    (localize [:generic/cookies])
    [:div
     [:div.container
      [:div.page-header "\t"]
      [:h2 "Cookies"]
      [:p "..."]]]))

(defn reagent-example [req]
  (vu/render-content-with
    req
    "Reagent Example"
    [:div
     [:div#app]
     [:script {:src "/js/main/main.js"}]
     [:script {:type "text/javascript"} "{{namespace}}.core.init();"]]))
