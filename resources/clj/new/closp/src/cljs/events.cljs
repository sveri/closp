(ns {{namespace}}.events
  (:require [re-frame.core :as rf]
            ["mobile-detect" :as mobile-detect]
            [{{namespace}}.db :as db]))

(rf/reg-event-db
 ::initialize-db
 (fn  [_ _]
   (merge db/default-db :is-mobile? (-> (mobile-detect. js/window.navigator.userAgent) .mobile some?))))

(rf/reg-event-db
 ::set-active-panel
 (fn [db [_ active-panel]]
   (assoc db :active-panel active-panel)))
