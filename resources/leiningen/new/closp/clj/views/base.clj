(ns {{ns}}.views.base
  (:require [ring.middleware.anti-forgery :as af]
            [hiccup.page :refer [html5 include-css include-js]]
            [noir.session :as sess]))



(defn merge-flash-messages
  "Expects a map containing keys and a values which will be put into the sessions flash"
  [messages]
  (doseq [m messages]
    (sess/flash-put! (key m) (val m))))

(defn flash-result [message div-class]
  (merge-flash-messages {:flash-message message :flash-alert-type div-class}))


(defn af-token []
  [:input {:name "__anti-forgery-token", :type "hidden", :value af/*anti-forgery-token*}])

(defn merge-active [page expected-page]
  (if (= page expected-page) "active" ""))


(defn menu [{:keys [uri role nexturl identity registration-allowed? localize]}]
  [:nav.navbar.fixed-top.navbar-expand-lg.navbar-light.bg-light
   [:a.navbar-brand {:href "/"} "{{name}}"]
   [:button.navbar-toggler
    {:type          "button", :data-toggle "collapse",
     :data-target   "#navbarTop", :aria-controls "navbarTop",
     :aria-expanded "false", :aria-label "Toggle navigation"}
    [:span {:class "navbar-toggler-icon"}]]
   [:div#navbarTop.collapse.navbar-collapse
    [:ul.navbar-nav.w-100
     [:li.nav-item {:class (merge-active uri "/reagent-example")}
      [:a {:class "nav-link ", :href "/reagent-example"} "Reagent Example"]]

     (when (= role "admin")
       [:li.nav-item.dropdown.mr-auto
        [:a#adminDropDownLink.nav-link.dropdown-toggle {:href "#", :data-toggle "dropdown", :aria-haspopup "true", :aria-expanded "false"}
         "Admin"]
        [:div.dropdown-menu.dropdown-menu-right {:aria-labelledby "adminDropDownLink"}
         [:a.dropdown-item {:href "/admin/users" :class (merge-active uri "/admin/users")} (localize [:user/users])]]])

     (if identity
       [:li.nav-item.dropdown.ml-auto
        [:a#profileDropdownLink.nav-link.dropdown-toggle {:href "#", :data-toggle "dropdown", :aria-haspopup "true", :aria-expanded "false"}
         identity]
        [:div.dropdown-menu.dropdown-menu-right {:aria-labelledby "profileDropdownLink"}
         [:a.dropdown-item {:href "/user/changepassword"} (localize [:user/change_password])]
         [:a.dropdown-item {:href "/user/logout"} (localize [:generic/logout])]]]

       [:form.form-inline.my-2.my-md-0.ml-auto {:action "/user/login", :method "POST"}
        (af-token)
        [:input {:name "nexturl", :type "hidden", :value nexturl}]
        [:input#upper_email.form-control {:type "text", :name "username" :placeholder (localize [:generic/email])}]
        [:input#upper_password.form-control {:type "password", :name "password" :placeholder (localize [:generic/password])}]
        [:button {:type "submit", :class "btn btn-primary"} (localize [:user/signin])]
        (when registration-allowed?
          [:a {:href "/user/signup", :class "btn btn-success"} (localize [:user/signup])])])]]])


(defn footer [uri localize]
  [:div#footer-menu-wrapper.container.navbar.navbar-default.navbar-expand-lg
   [:ul.navbar-nav.w-100
    [:li.nav-item {:class (merge-active uri "/contact")}
     [:a {:class "nav-link ", :href "/contact"} (localize [:generic/contact])]]
    [:li.nav-item {:class (merge-active uri "/tos")}
     [:a {:class "nav-link ", :href "/tos"} (localize [:generic/tos])]]
    [:li.nav-item {:class (merge-active uri "/cookies")}
     [:a {:class "nav-link ", :href "/cookies"} (localize [:generic/cookies])]]]])


(defn hicc-base [title {:keys [flash-message flash-alert-type css plain-js uri localize] :as opts} content]
  (html5 [:head
          [:title title]
          (include-css "/css/bootstrap.min.css")
          (include-css "/css/screen.css")
          (when css (include-css css))]

         [:body
          [:header (menu opts)]

          [:main#wrap.container {:role "main"}
           [:div#content (when flash-message [:div#flash-message.alert {:class flash-alert-type} flash-message])
            content]]

          [:footer.footer (footer uri localize)]

          (include-js "/js/jquery-3.2.1.slim.min.js")
          (include-js "/js/bootstrap.min.js")
          (include-js "/js/compiled/app.js")
          (when plain-js [:div plain-js])]))




(defn render [title {:keys [localize config uri css plain-js nexturl] :as req} content]
  (hicc-base title
             {:localize localize :identity (sess/get :identity)
              :role (sess/get :role) :registration-allowed? (:registration-allowed? config)
              :captcha-enabled? (:captcha-enabled? config)
              :flash-message (sess/flash-get :flash-message)
              :flash-alert-type (sess/flash-get :flash-alert-type)
              :uri uri :css css :plain-js plain-js
              :nexturl (get (get req :query-params {}) "nexturl" "/")}
             content))
