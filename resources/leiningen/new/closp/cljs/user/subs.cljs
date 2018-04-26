(ns {{ns}}.user.subs
  (:require [re-frame.core :as rf]))


(rf/reg-sub
  ::signup-email
  (fn [db _]
    (-> db :signup :email)))

(rf/reg-sub
  ::login-email
  (fn [db _]
    (-> db :login :email)))

(rf/reg-sub
  ::signup-email-validation
  (fn [db _]
    (-> db :signup :email-validation)))

(rf/reg-sub
  ::signup-password
  (fn [db _]
    (-> db :signup :password)))

(rf/reg-sub
  ::login-password
  (fn [db _]
    (-> db :login :password)))

(rf/reg-sub
  ::signup-password-validation
  (fn [db _]
    (-> db :signup :password-validation)))

(rf/reg-sub
  ::signup-password-confirmation
  (fn [db _]
    (-> db :signup :password-confirmation)))

(rf/reg-sub
  ::signup-password-confirmation-validation
  (fn [db _]
    (-> db :signup :password-confirmation-validation)))


(rf/reg-sub
  ::generic-login-error
  (fn [db _] (-> db :login :generic-error)))

(rf/reg-sub
  ::user
  (fn [db _] (-> db :user)))

(rf/reg-sub
  ::role
  (fn [db _] (-> db :user :role)))





(rf/reg-sub
  ::cp-current-password
  (fn [db _]
    (-> db :cp :current-password)))

(rf/reg-sub
  ::cp-current-password-validation
  (fn [db _]
    (-> db :cp :current-password-validation)))

(rf/reg-sub
  ::cp-password
  (fn [db _]
    (-> db :cp :password)))

(rf/reg-sub
  ::cp-password-validation
  (fn [db _]
    (-> db :cp :password-validation)))

(rf/reg-sub
  ::cp-password-confirmation
  (fn [db _]
    (-> db :cp :password-confirmation)))

(rf/reg-sub
  ::cp-password-confirmation-validation
  (fn [db _]
    (-> db :cp :password-confirmation-validation)))