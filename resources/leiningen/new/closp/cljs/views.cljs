(ns {{ns}}.views
  (:require [re-frame.core :as re-frame]
            [{{ns}}.subs :as subs]
            [{{ns}}.routes :as routes]))


(defn home-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    (fn []
        [:div (str "Hello from " @name ". This is the Home Page.")
         [:div [:a {:href (routes/url-for :about)} "go to About Page"]]])))

(defn about-panel []
  (fn []
      [:div "This is the About Page."
       [:div [:a {:href (routes/url-for :home)} "go to Home Page"]]]))


(defmulti panels identity)
(defmethod panels :home-panel [] [home-panel])
(defmethod panels :about-panel [] [about-panel])
(defmethod panels :default [] [:div "This is default route"])


(defn main-panel []
  (let [active-panel (re-frame/subscribe [::subs/active-panel])]
    (panels active-panel)))
