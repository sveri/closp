(ns {{namespace}}.views.user
  (:require [{{namespace}}.views.util :as vu]))

(def ^:const password-min-length 6)

(defn login-page [{:keys [nexturl error]} {:keys [localize] :as req}]
  (vu/render-content-with
    req
    (localize [:user/signin])
    [:div#login.row.center-align
     [:div.col.m2.l4.blank]

     [:div.col.s12.m8.l4.yield
      [:h3.header (localize [:user/signin])]

      [:form#login-form {:action "/user/login", :method "POST"}
       (vu/af-token)
       (when nexturl [:input {:name "nexturl", :type "hidden", :value nexturl}])
       (when error [:blockquote#error error])

       [:div.input-field
        [:label {:for "email"} (localize [:generic/email])]
        [:input#email.validate {:type     "email", :name "email" :placeholder (localize [:generic/email])
                                :tabIndex 1 :required true :autofocus true}]
        [:span.helper-text {:data-error (localize [:user/email_invalid])}]]
       [:div.input-field
        [:label {:for "password"} (localize [:generic/password])]
        [:input#password.validate {:type        "password", :name "password", :minlength 1
                                   :placeholder (localize [:generic/password]) :tabIndex 2 :required true}]
        [:span.helper-text {:data-error (localize [:user/pass_min_length] [1])}]]

       [:button.btn.waves-effect.waves-light {:type "submit" :tabIndex 3} (localize [:user/signin])
        [:i.material-icons.right "send"]]]]

     [:div.col.m2.l4.blank]]))


(defn signup-page [{:keys [captcha-public-key captcha-enabled? email-exists password-error email-invalid
                           re-captcha-invalid email displayname]}
                   {:keys [localize] :as req}]
  (vu/render-content-with
    req
    (localize [:user/signup])
    [:div#signup
     [:div.row
      [:div.col.m2.l4.blank]

      [:div.col.s12.m8.l4.yield
       [:form {:action "/user/signup", :method "POST"}
        (vu/af-token)
        [:h3.header (localize [:user/register])]

        (when email-exists [:blockquote#error email-exists])
        (when password-error [:blockquote#error password-error])
        (when email-invalid [:blockquote#error email-invalid])
        (when re-captcha-invalid [:blockquote#error re-captcha-invalid])

        [:div.input-field
         [:label {:for "displayname"} (localize [:generic/displayname])]
         [:input#displayname.validate {:type     "text", :name "displayname" :placeholder (localize [:generic/displayname])
                                       :tabIndex 1 :required true :autofocus true :value displayname}]
         [:span.helper-text {:data-error (localize [:user/displayname_invalid])}]]

        [:div.input-field
         [:label {:for "email"} (localize [:generic/email])]
         [:input#email.validate {:type     "email", :name "email" :placeholder (localize [:generic/email])
                                 :tabIndex 2 :required true :value email}]
         [:span.helper-text {:data-error (localize [:user/email_invalid])}]]

        [:div.input-field
         [:label {:for "password"} (localize [:generic/password])]
         [:input#password.validate {:type        "password", :name "password", :minlength password-min-length
                                    :placeholder (localize [:generic/password]) :tabIndex 3 :required true}]
         [:span.helper-text {:data-error (localize [:user/pass_min_length] [password-min-length])}]]

        (when captcha-enabled?
          [:div
           [:input#re-captcha-value {:type "hidden" :name "re-captcha-token"}]
           [:script {:type "text/javascript", :src (str "https://www.google.com/recaptcha/api.js?render=" captcha-public-key)}]
           [:script "grecaptcha.ready(function() {grecaptcha.execute('" captcha-public-key "', {action: 'login'})
                      .then(function(token) {document.getElementById(\"re-captcha-value\").value = token;});});"]])
        [:button.btn.waves-effect.waves-light {:type "submit" :tabIndex 3} (localize [:user/register])
         [:i.material-icons.right "send"]]]]

      [:div.col.m2.l4.blank]]]))

(defn changepassword-page [{:keys [password-error confirm-error old-error]} {:keys [localize] :as req}]
  (vu/render-content-with
    req
    (localize [:user/change_password])
    [:div#changepassword
     [:div.row
      [:div.col.m2.l4.blank]
      [:div.col.s12.m8.l4.yield
       [:form#changepassword-form {:action "/user/changepassword", :method "POST"}
        (vu/af-token)
        [:h3 (localize [:user/change_password])]

        (when old-error [:blockquote#error old-error])
        (when password-error [:blockquote#error password-error])
        (when confirm-error [:blockquote#error confirm-error])

        [:div.input-field
         [:label {:for "oldpassword"} (localize [:user/current_password])]
         [:input#oldpassword.validate {:type        "password", :name "oldpassword"
                                       :placeholder (localize [:user/current_password]) :tabIndex 1 :required true}]]


        [:div.input-field
         [:label {:for "password"} (localize [:user/new_password])]
         [:input#password.validate {:type        "password", :name "password", :minlength password-min-length
                                    :placeholder (localize [:user/new_password]) :tabIndex 2 :required true}]
         [:span.helper-text {:data-error (localize [:user/pass_min_length] [password-min-length])}]]

        [:div.input-field
         [:label {:for "confirm"} (localize [:user/new_password_confirm])]
         [:input#confirm.validate {:type        "password", :name "confirm", :minlength password-min-length
                                   :placeholder (localize [:user/new_password_confirm]) :tabIndex 3 :required true}]
         [:span.helper-text {:data-error (localize [:user/pass_min_length] [password-min-length])}]]

        [:button.btn.waves-effect.waves-light {:type "submit" :tabIndex 4} (localize [:user/change_password])
         [:i.material-icons.right "send"]]]]

      [:div.col.m2.l4.blank]]]))


(defn really-delete-page [{:keys [email]} {:keys [localize] :as req}]
  (vu/render-content-with
    req
    (localize [:user/really_delete] [email])
    [:form {:method "post", :action "/admin/user/delete"}
     (vu/af-token)
     [:input {:type "hidden" :name "user-email", :value email}]
     [:p [:b (localize [:user/really_delete] [email])]]
     [:input#really-delete-cancel.btn.waves-effect.waves-light
      {:type "submit" :name "delete_cancel", :value (localize [:generic/cancel])}]
     [:input#really-delete-delete.btn.waves-effect.waves-light.red.right
      {:type "submit" :name "delete_cancel", :value (localize [:generic/delete])}]]))


(defn admin-page [{:keys [users roles filter email-exists email displayname password-error email-invalid]}
                  {:keys [localize] :as req}
                  toast-message]
  (vu/render-content-with
    req
    (localize [:admin/title])
    [:div.section
     [:h5 (localize [:admin/add_user])]
     [:form#admin-add-user-form {:action "/admin/user/add", :method "POST"}
      (vu/af-token)
      (when email-exists [:blockquote#error email-exists])
      (when password-error [:blockquote#error password-error])
      (when email-invalid [:blockquote#error email-invalid])

      [:div.row
       [:div.input-field.col.s12.m3
        [:label {:for "displayname"} (localize [:generic/displayname])]
        [:input#displayname.validate {:type     "text", :name "displayname" :placeholder (localize [:generic/displayname])
                                      :tabIndex 1 :required true :autofocus true :value displayname}]
        [:span.helper-text {:data-error (localize [:user/displayname_invalid])}]]

       [:div.input-field.col.s12.m3
        [:label {:for "email"} (localize [:generic/email])]
        [:input#email.validate {:type     "email", :name "email" :placeholder (localize [:generic/email])
                                :tabIndex 2 :required true :value email}]
        [:span.helper-text {:data-error (localize [:user/email_invalid])}]]

       [:div.input-field.col.s12.m3
        [:label {:for "password"} (localize [:generic/password])]
        [:input#password.validate {:type        "password", :name "password", :minlength password-min-length
                                   :placeholder (localize [:generic/password]) :tabIndex 3 :required true}]
        [:span.helper-text {:data-error (localize [:user/pass_min_length] [password-min-length])}]]

       [:div.input-field.col.s12.m3
        [:button.btn.waves-effect.waves-light {:type "submit" :tabIndex 4} (localize [:admin/add_user])
         [:i.material-icons.right "send"]]]]]

     [:div.divider]

     [:div.section
      [:h5 (localize [:admin/filter])]
      [:form {:action "/admin/users", :method "GET"}
       [:div.row
        [:div.input-field.col.s12.m4
         [:input#username-filter {:type "text", :name "filter" :value filter
                                  :placeholder (localize [:generic/search])}]]

        [:div.col.s12.m3
         [:button.btn.waves-effect.waves-light.col {:type "submit"} (localize [:admin/filter])
          [:i.material-icons.right "send"]]]]]]

     [:div.divider]

     [:div.section
      [:h5 (localize [:user/users])]
      [:table#admin-users-table.responsive-table
       [:thead
        [:tr
         [:th (localize [:generic/email])]
         [:th (localize [:user/displayname])]
         [:th (localize [:user/role])]
         [:th (localize [:admin/active])]]]
       [:tbody
        (for [user users]
          [:form {:field "user-row", :class "tr", :method "post", :action "/admin/user/update"}
           (vu/af-token)
           [:input {:type "hidden", :field "username-hidden", :name "user-email", :value (:email user)}]
           [:tr
            [:td (:email user)]
            [:td (:displayname user)]
            [:td [:div.input-field
                  [:select.browser-default {:name "role"}
                   (for [role roles]
                     [:option (merge {:value role} (if (= role (:role user)) {:selected true} {})) role])]]]
            [:td [:label [:input (merge {:type "checkbox", :name "active"}
                                        (if (:is_active user) {:checked "checked"} {}))]
                  [:span.for-active-checkbox ""]]]
            [:td
             [:button.btn.waves-effect.waves-light {:name "update_delete" :value (localize [:admin/update])
                                                    :type "submit" :data-test "update-button"}
              (localize [:admin/update])]]
            [:td
             [:button.btn.waves-effect.waves-light.red.lighten-3
              {:name "update_delete" :value (localize [:generic/delete])
               :type "submit"} (localize [:generic/delete])]]]])]]]]
    (when toast-message toast-message)))





