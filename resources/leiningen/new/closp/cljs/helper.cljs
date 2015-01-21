(ns {{ns}}.helper
  (:require [cljs.core.async :refer [chan close! put!]]
            [goog.events :as events]
            [goog.fx :as fx]
            [goog.fx.dom :as fx-dom]
            [goog.dom :as gdom]
            [goog.dom.forms :as gforms]
            [goog.net.XhrIo :as xhr]
            ;[ajax.core :as ajax]
            )
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn timeout [ms]
      (let [c (chan)]
           (js/setTimeout (fn [] (close! c)) ms)
           c))

(defn get-value [elem]
      (gforms/getValue (gdom/getElement elem)))

(defn get-elem [id] (gdom/getElement id))

(defn get-attr [elem attr] (.getAttribute elem attr))

;(defn send [url method content]
;  (let [ch (chan 1)]
;    (ajax/ajax-request
;      {:uri     url
;       :method  method
;       :params  content
;       ;:format (ajax/transit-request-format)
;       :response-format (ajax/transit-response-format)
;       :handler (fn [resp]
;                  (put! ch resp))})
;    ch))
;
;(defn post-async [url content]
;  (send url :post content))
;
;(defn get-async [url]
;  (send url :get nil))
;
;(defn prevent-default [e]
;  (.preventDefault e))

(defn cut-str-at
      "Cuts string at (- length 3) and adds \"...\" to the end of the returned string"
      [s length]
      (if (> (count s) length)
        (str (.substring s 0 (- length 3)) "...")
        s))

(defn fade-out
      ([] (fade-out 1000 nil))
      ([tm] (fade-out tm nil))
      ([tm callback]
        (fn [node]
            (let [anim (fx-dom/FadeOut. node tm)]
                 (when callback
                       (events/listen anim js/goog.fx.Animation.EventType.END callback))
                 (. anim (play))))))

(defn fade-in
      ([] (fade-in 1000 nil))
      ([tm] (fade-in tm nil))
      ([tm callback]
        (fn [node]
            (let [anim (fx-dom/FadeIn. node tm)]
                 (when callback
                       (events/listen anim js/goog.fx.Animation.EventType.END callback))
                 (. anim (play))))))
