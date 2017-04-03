(ns de.sveri.gup.views.user
  (:require [de.sveri.gup.views.base :as v]))


(defn login-page [{:keys [nexturl error]} {:keys [localize] :as req}]
  (v/render
    (localize [:user/signin]) req
    [:div
     [:div {:class "container-fluid block-center", :id "login"}
      [:div {:class "row"}
       [:div {:class "col-md-3"}]
       [:div {:class "col-md-6"}

        [:form {:class "form-horizontal", :role "form", :action "/user/login", :method "POST"}
         (v/af-token)
         (when nexturl [:input {:name "nexturl", :type "hidden", :value nexturl}])
         (when error [:div {:id "error", :class "alert alert-danger"} error])

         [:div {:class "form-group"}
          [:label {:for "email", :class "col-sm-2 control-label"}]
          [:div {:class "col-sm-10"}
           [:input {:type "text", :class "form-control", :name "username", :id "email", :placeholder (localize [:generic/email])}]]]
         [:div {:class "form-group"}
          [:label {:for "password", :class "col-sm-2 control-label"}]
          [:div {:class "col-sm-10"}
           [:input {:type "password", :class "form-control", :name "password", :id "password", :placeholder (localize [:generic/password])}]]]
         [:div {:class "form-group"}
          [:div {:class "col-sm-offset-2 col-sm-10"}
           [:button {:type "submit", :class "btn btn-default"} (localize [:user/signin])]]]]]
       [:div {:class "col-md-3"}]]]]))

(defn changepassword-page [{:keys [pass-error confirm-error old-error]} {:keys [localize] :as req}]
  (v/render
    (localize [:user/change_password]) req
    [:div
     [:div {:id "changepassword", :class "container-fluid block-center"}
      [:div {:class "row"}
       [:div {:class "col-md-3"}]
       [:div {:class "form-group col-md-6"}
        [:form {:action "/user/changepassword", :method "POST", :role "form"}
         (v/af-token)
         [:h3 (localize [:user/change_password])]
         [:div {:class "form-group"}
          [:label {:for "oldpassword"} (localize [:user/current_password])]
          (when old-error [:div {:id "old-pass-error", :class "alert alert-danger"} old-error])
          [:p
           [:input {:class "form-control", :id "oldpassword", :name "oldpassword", :tabindex "2", :type "password", :autocomplete "off"}]]]
         [:div {:class "form-group"}
          [:label {:for "password"} (localize [:user/new_password])]
          (when pass-error [:div {:id "pass-error", :class "alert alert-danger"} pass-error])
          [:p
           [:input {:class "form-control", :id "password", :name "password", :tabindex "2", :type "password", :autocomplete "off"}]]]
         [:div {:class "form-group"}
          [:label {:for "confirm"} (localize [:user/new_password_confirm])]
          (when confirm-error [:div {:id "confirm-error", :class "alert alert-danger"} confirm-error])
          [:p
           [:input {:class "form-control", :id "confirm", :name "confirm", :tabindex "3", :type "password", :autocomplete "off"}]]]
         [:div {:class "form-group"}
          [:input {:class "btn btn-primary", :tabindex "4", :type "submit", :value (localize [:user/change_password])}]]]]
       [:div {:class "col-md-3"}]]]]))


(defn really-delete-page [{:keys [id username]} {:keys [localize] :as req}]
  (v/render
    (localize [:user/really_delete]) req
    [:div
     [:form {:field "user-row", :class "tr", :method "post", :action "/admin/user/delete"}
      (v/af-token)
      [:input {:type "hidden", :field "username-hidden", :name "user-id", :value id}]
      [:p [:b (localize [:user/really_delete] [username])]]
      [:span {:field "submit", :class "td"}
       [:input {:type "submit", :name "delete_cancel", :value (localize [:generic/cancel]), :class "btn btn-primary"}]]
      [:span {:field "submit", :class "td"}
       [:input {:type "submit", :name "delete_cancel", :value (localize [:generic/delete]), :class "btn btn-danger"}]]]]))


(defn signup-page [{:keys [captcha-public-key captcha-enabled? email-error pass-error confirm-error captcha-error email]}
                   {:keys [localize] :as req}]
  (v/render
    (localize [:user/signup]) req
    [:div
     [:div {:id "signup", :class "container-fluid block-center"}
      [:div {:class "row"}
       [:div {:class "col-md-3"}]
       [:div {:class "form-group col-md-6"}
        [:form {:action "/user/signup", :method "POST", :role "form"}
         (v/af-token)
         [:h3 (localize [:user/register])]
         [:div {:class "form-group"}
          [:label {:for "email"} (localize [:generic/email])]
          (when email-error[:div {:id "email-error", :class "alert alert-danger"} email-error])
          [:p
           [:input {:id "email", :class "form-control", :tabindex "1", :name "email", :value email, :type "text", :autocomplete "off"}]]]
         [:div {:class "form-group"}
          [:label {:for "password"} (localize [:generic/password])]
          (when pass-error [:div {:id "pass-error", :class "alert alert-danger"} pass-error])
          [:p
           [:input {:class "form-control", :id "password", :name "password", :tabindex "2", :type "password", :autocomplete "off"}]]]
         [:div {:class "form-group"}
          [:label {:for "confirm"} (localize [:generic/password_confirm])]
          (when confirm-error [:div {:id "confirm-error", :class "alert alert-danger"} confirm-error])
          [:p
           [:input {:class "form-control", :id "confirm", :name "confirm", :tabindex "3", :type "password", :autocomplete "off"}]]]
         (when captcha-enabled?
           [:div {:class "form-group"}
            (when captcha-error [:div {:class "alert alert-danger"} captcha-error])
            [:script {:type "text/javascript", :src "http://www.google.com/recaptcha/api/challenge?k="}]])
         [:div {:class "form-group"}
          [:input {:class "btn btn-primary", :tabindex "4", :type "submit", :value (localize [:user/register])}]]]]
       [:div {:class "col-md-3"}]]]]))

(defn admin-page [{:keys [users roles admin_title email-error email pass-error confirm-error]} {:keys [localize] :as req}]
  (v/render
    (localize [:admin/title]) (merge req {:css "/css/admin.css"})
    [:div
     [:div {:id "admin"}
      [:div {:id "add-user-div"}
       [:form {:action "/admin/user/add", :method "POST", :role "form", :class "form-inline"}
        (v/af-token)
        [:div {:class "form-group"}
         (when email-error [:div {:id "email-error", :class "alert alert-danger"} email-error])
         [:input {:class "form-control", :id "email", :name "email", :tabindex "1", :type "text", :autocomplete "off", :placeholder "Email" :value email}]]
        [:div {:class "form-group"}
         (when pass-error [:div {:id "pass-error", :class "alert alert-danger"} pass-error])
         [:input {:class "form-control", :id "password", :name "password", :tabindex "2", :type "password", :placeholder (localize [:generic/password]), :autocomplete "off"}]]
        [:div {:class "form-group"}
         (when confirm-error [:div {:id "confirm-error", :class "alert alert-danger"} confirm-error])
         [:input {:class "form-control", :id "confirm", :name "confirm", :tabindex "3", :type "password", :placeholder (localize [:generic/password_confirm]), :autocomplete "off"}]]
        [:button {:class "btn btn-primary", :tabindex "4", :type "submit"} (localize [:admin/add_user])]]]

      [:hr]

      [:form {:class "form-inline padding", :role "form", :action "/admin/users", :method "GET"}
       (v/af-token)
       [:div {:class "form-group"}
        [:label {:class "sr-only", :for "username-filter"} (localize [:user/username])]
        [:input {:type "text", :class "form-control", :id "username-filter", :name "filter", :placeholder (localize [:user/username])}]]
       [:button {:type "submit", :class "btn btn-default"} (localize [:admin/filter])]]
      [:br]

      [:div {:class "table", :field "table"}
       [:div {:class "tr", :field "table-header-row"}
        [:span {:class "td", :field "header-entry"} [:b (localize [:user/username])]]
        [:span {:class "td", :field "header-entry"} [:b (localize [:user/role])]]
        [:span {:class "td", :field "header-entry"} [:b (str (localize [:admin/active]) "?")]]]

       (for [user users]
         [:form {:field "user-row", :class "tr", :method "post", :action "/admin/user/update"}
          (v/af-token)
          [:input {:type "hidden", :field "username-hidden", :name "user-id", :value (:id user)}]
          [:span {:field "username", :class "td"} (:email user)]
          [:span {:field "role", :class "td"}
           [:select {:field "role-select", :name "role"}
            (for [role roles]
              [:option (merge {:value role } (if (= role (:role user)) {:selected "selected"} {}))  role])]]
          [:span {:field "active", :class "td"}
           [:input (merge {:type "checkbox", :name "active"} (if (:is_active user) {:checked "checked"} {}))]]
          [:span {:field "submit", :class "td"}
           [:input {:type "submit", :name "update_delete", :value (localize [:admin/update]), :class "btn btn-primary"}]]
          [:span {:field "submit", :class "td"}
           [:input {:type "submit", :name "update_delete", :value (localize [:generic/delete]), :class "btn btn-danger"}]]])]]]))





