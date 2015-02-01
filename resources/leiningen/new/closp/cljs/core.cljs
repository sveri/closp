(ns {{ns}}.core
  (:require [reagent.core :as reagent :refer [atom]]
            [{{ns}}.helper :as h]
            [{{ns}}.cljxcore :as cljx]))

(defonce app-state (atom {:text "Hello Chestnut!"}))

(defn child [name]
      [:p "Hi, I am " name])

(defn childcaller []
      [child "Foo Bar"])


(defn main []
      (cljx/foo-cljx "hello from js")
      (reagent/render-component (fn [] [childcaller]) (h/get-elem "app")))
