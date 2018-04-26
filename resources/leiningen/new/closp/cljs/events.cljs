(ns {{ns}}.events
  (:require [re-frame.core :as rf]
            [{{ns}}.third-party.http-fx]
            [{{ns}}.db :as def-db]
            [{{ns}}.user.events :as u-e]))


(rf/reg-event-fx
  ::initialize-db
  (fn [_ [_ _]]
      {:http-xhrio {:method     :get
                    :uri        "/api/data/initial"
                    :on-success [::u-e/store-user-from-initial-data]
                    :on-failure [::initial-data-error]}
       :db         def-db/default-db}))



(rf/reg-event-fx
  ::initial-data-error
  (fn [{db :db} [_ response]]
      {:db (assoc db :generic-error (:error response))
       :navigate-to :login}))
