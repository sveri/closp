(ns {{ns}}.effects
  (:require [pushy.core :as pushy]
            [re-frame.core :as rf]
            [{{ns}}.routes :as r]
            [{{ns}}.helper :as h]))


(rf/reg-fx :navigate-to #(do (pushy/set-token! r/history (r/url-for %))
                             (r/dispatch-route {:handler %})))

(rf/reg-fx :timer (fn [ev timer] (js/setInterval #(h/>evt ev) (or timer 3000))))
