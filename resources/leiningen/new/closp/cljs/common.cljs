(ns {{ns}}.common
  (:require [re-frame.core :as rf]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [{{ns}}.user.helper :as uh]))



; generic stuff


(rf/reg-event-db
  ::set-active-panel
  (fn-traced [db [_ active-panel]]
    (assoc db :active-panel active-panel)))


(rf/reg-sub
  ::generic-error-sub
  (fn [db]
    (:generic-error db)))


(rf/reg-event-db
  ::clear-generic-error
  (fn [db _]
    (dissoc db :generic-error)))


(defn set-generic-success-message [db msg]
  (assoc db :generic-success msg))


(rf/reg-sub
  ::generic-success-sub
  (fn [db]
    (:generic-success db)))


(rf/reg-event-db
  ::clear-generic-success
  (fn [db _]
    (dissoc db :generic-success)))


; loading screen

(defn show-loading-screen [db]
  (assoc db :show-loading-screen true))

(defn hide-loading-screen [db]
  (assoc db :show-loading-screen false))

(rf/reg-sub
  ::loading-screen
  (fn [db _] (:show-loading-screen db)))