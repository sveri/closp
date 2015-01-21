(ns {{ns}}.core
  (:require [reagent.core :as reagent :refer [atom]]
            [{{ns}}.helper :as h]))

(defonce app-state (atom {:text "Hello Chestnut!"}))

(defn child [name]
      [:p "Hi, I am " name])

(defn childcaller []
      [child "Foo Bar"])


(defn main []
      (reagent/render-component (fn [] [childcaller]) (h/get-elem "app")))
