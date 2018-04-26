(ns {{ns}}.helper
  (:require [goog.dom :as gdom]
            [goog.dom.forms :as gforms]
            [cuerdas.core :as cc]))


(def <sub (comp deref re-frame.core/subscribe))

(def >evt re-frame.core/dispatch)

(def >evt-sync re-frame.core/dispatch-sync)

(defn get-value-of-event [e]
  (.-value (.-target e)))

(defn get-value [elem]
  (gforms/getValue (gdom/getElement elem)))

(defn get-af-token []
  (gforms/getValue (gdom/getElement "__anti-forgery-token")))

(defn get-elem [id] (gdom/getElement id))

(defn get-attr [elem attr] (.getAttribute elem attr))

(defn clean-input [s]
  (cc/lower (cc/trim s)))

