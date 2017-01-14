(ns {{ns}}.core
  (:require [reagent.core :as reagent :refer [atom]]
            [{{ns}}.helper :as h]))


;;; Some non-DB state
(def state (atom {:name "You" :age 22}))

(defn p1 [name]
      [:div (str "Hi there " name)])

(defn p2 [age]
      [:div (str "You are " age)])

;;; Uber component, contains/controls stuff and younguns.
(defn uber []
      (let [name (:name @state)
            age (:age @state)]
           [:div
            [p1 name]
            [p2 age]]))


(defn ^:export main []
      (reagent/render-component (fn [] [uber]) (h/get-elem "app")))