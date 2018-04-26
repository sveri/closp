(ns {{ns}}.user.views
  (:require [{{ns}}.cljc.locale :as loc]
            [{{ns}}.helper :refer [>evt <sub]]
            [{{ns}}.user.events :as ev]
            [{{ns}}.user.subs :as subs]
            [{{ns}}.helper :as h]
            [re-pressed.core :as rp]
            [{{ns}}.routes :as routes]))


(defn welcome-header []
  [:div
   [:h1 "Welcome to {{name}}"]])

; login
(defn login->keylisteners []
  (>evt
    [::rp/set-keydown-rules
     {:event-keys [[[::ev/login] [{:which 13}]]]
      :always-listen-keys [{:which 13}]}]))


(defn login-panel []
  (login->keylisteners)
  (let [generic-login-error (<sub [::subs/generic-login-error])]
    [:div
     [welcome-header]
     [:div#signup.container-fluid.block-center
      [:div.row
       [:div.col-md-9]
       [:divcol-md-3
        [:h3 (loc/localize [:user/signin])]

        (when generic-login-error [:div.alert.alert-danger generic-login-error])

        [:div.form-group
          [:input.form-control
           {:type "email"
            :value (<sub [::subs/login-email]) :onChange #(>evt [::ev/set-login-email (h/get-value-of-event %)])}]]

        [:div.form-group
          [:input.form-control
           {:type "password"
            :value (<sub [::subs/login-password]) :onChange #(>evt [::ev/set-login-password (h/get-value-of-event %)])}]]


        [:div.form-group
         [:button.btn.btn-primary {:onClick #(>evt [::ev/login])} (loc/localize [:user/signin])]]
        [:div.form-group
         [:a.btn.btn-default {:href (routes/url-for :signup)} (loc/localize [:user/signup])]]]]]]))



; signup

(defn signup->keylisteners []
  (>evt
    [::rp/set-keydown-rules
     {:event-keys [[[::ev/signup] [{:which 13}]]]
      :always-listen-keys [{:which 13}]}]))


(defn with-invalid [validate m]
  (merge (when validate {:class "is-invalid"}) m))

(defn signup-panel []
  (signup->keylisteners)
  (let [email (<sub [::subs/signup-email])
        email-validation (<sub [::subs/signup-email-validation])
        password (<sub [::subs/signup-password])
        password-confirmation (<sub [::subs/signup-password-confirmation])
        password-validation (<sub [::subs/signup-password-validation])
        password-confirmation-validation (<sub [::subs/signup-password-confirmation-validation])]
    [:div
     [welcome-header]
     [:div.row
      [:div.col-md-6]
      [:div.col-md-6
       [:div
        [:h3 (loc/localize [:user/register])]

        [:div.form-group
         [:input.form-control
          (with-invalid
            email-validation
            {:type  "email", :placeholder (loc/localize [:generic/email])
             :value email :onChange #(>evt [::ev/set-signup-email (h/get-value-of-event %)])})]]

        [:div.form-group
         [:input.form-control
          (with-invalid
            password-validation
            {::type "password" :placeholder (loc/localize [:user/password])
             :value password :onChange #(>evt [::ev/set-signup-password (h/get-value-of-event %)])})]]

        [:div.form-group
         [:input.form-control
          (with-invalid
            password-confirmation-validation
            {:type  "password" :placeholder (loc/localize [:user/password_confirm])
             :value password-confirmation :onChange #(>evt [::ev/set-signup-password-confirmation (h/get-value-of-event %)])})]]


        [:div.form-group
         [:button.btn.btn-primary
          {:onClick  #(>evt [::ev/signup])
           :disabled (when (or email-validation password-validation password-confirmation-validation
                               (empty? email) (empty? password) (empty? password-confirmation))
                       "disabled")}
          (loc/localize [:user/register])]]
        [:div.form-group
         [:a.btn.btn-default {:href (routes/url-for :login)} (loc/localize [:user/signin])]]]]]]))


; changepassword

(defn changepassword->keylisteners []
  (>evt
    [::rp/set-keydown-rules
     {:event-keys [[[::ev/change-password] [{:which 13}]]]
      :always-listen-keys [{:which 13}]}]))


(defn change-password-panel []
  (changepassword->keylisteners)
  (let [current-password (<sub [::subs/cp-current-password])
        current-password-validation (<sub [::subs/cp-current-password-validation])
        password (<sub [::subs/cp-password])
        password-validation (<sub [::subs/cp-password-validation])
        password-confirmation (<sub [::subs/cp-password-confirmation])
        password-confirmation-validation (<sub [::subs/cp-password-confirmation-validation])]
    [:div.row
     [:div.col-md-3]
     [:div.col-md-6
      [:div
       [:h3 (loc/localize [:user/change_password])]

       [:div.form-group
        [:input.form-control
         (with-invalid
           current-password-validation
           {:type  "password", :placeholder (loc/localize [:user/current_password])
            :value current-password :onChange #(>evt [::ev/set-cp-current-password (h/get-value-of-event %)])})]]

       [:div.form-group
        [:input.form-control
         (with-invalid
           password-validation
           {::type "password" :placeholder (loc/localize [:user/password])
            :value password :onChange #(>evt [::ev/set-cp-password (h/get-value-of-event %)])})]]

       [:div.form-group
        [:input.form-control
         (with-invalid
           password-confirmation-validation
           {:type  "password" :placeholder (loc/localize [:user/password_confirm])
            :value password-confirmation :onChange #(>evt [::ev/set-cp-password-confirmation (h/get-value-of-event %)])})]]


       [:div.form-group
        [:button.btn.btn-primary
         {:onClick  #(>evt [::ev/change-password])
          :disabled (when (or current-password-validation password-validation password-confirmation-validation
                              (empty? current-password) (empty? password) (empty? password-confirmation))
                      "disabled")}
         (loc/localize [:user/change_password])]]]]

     [:div.col-md-3]]))