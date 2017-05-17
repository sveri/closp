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
  [:div
   [:div {:field "menu"}
    [:ul {:class "nav navbar-nav", :id "header-menu-wrapper"}
     [:li {:field "link-list" :class (merge-active uri "/reagent-example")}
      [:a {:href "/reagent-example"} "Example Page"]]
     (when (= role "admin")
       [:li {:class "dropdown"}
        [:a {:href "#", :class "dropdown-toggle", :data-toggle "dropdown"}
         "Admin"
         [:b {:class "caret"}]]
        [:ul {:class "dropdown-menu"}
         [:li {:field "link-list" :class (merge-active uri "/user")}
          [:a {:href "/admin/users"} "Users"]]
         [:li {:field "link-list" :class (merge-active uri "/crud/team/index")}
          [:a {:href "/crud/team/index"} "Teams"]]]])]
    (if identity
      [:div {:id "logout", :class "navbar-right"}
       [:ul {:class "navbar-right nav navbar-nav"}
        [:li {:class "dropdown"}
         [:a {:href "#", :class "dropdown-toggle", :data-toggle "dropdown"}
          identity
          [:b {:class "caret"}]]
         [:ul {:class "dropdown-menu"}
          [:li
           [:a {:href "/user/changepassword"} (localize [:user/change_password])]]
          [:li
           [:a {:href "/user/logout"} (localize [:generic/logout])]]]]]]
      [:div {:class "navbar-collapse collapse"}
       [:form {:class "navbar-form navbar-right", :role "form", :action "/user/login", :method "POST"}
        (af-token)
        [:input {:name "nexturl", :type "hidden", :value nexturl}]
        [:div {:class "form-group"}
         [:input {:id "upper_email", :type "text", :placeholder (localize [:generic/email]), :name "username", :class "form-control"}]]
        [:div {:class "form-group"}
         [:input {:id "upper_password", :type "password", :placeholder (localize [:generic/password]), :name "password", :class "form-control"}]]
        [:button {:type "submit", :class "btn btn-primary"} (localize [:user/signin])]
        (when registration-allowed?
          [:a {:href "/user/signup", :class "btn btn-success"} (localize [:user/signup])])]])]])

(defn footer []
  [:div
   [:div {:class "footer"}
    [:div {:class "container"}
     [:div {:class "navbar-collapse collapse"}
      [:ul {:class "nav navbar-nav", :id "header-menu-wrapper"}
       [:li {:field "link-list"}
        [:a {:href "/contact"} "Contact"]]
       [:li {:field "link-list"}
        [:a {:href "/tos"} "TOS"]]
       [:li {:field "link-list"}
        [:a {:href "/cookies"} "Cookies"]]]]]]])




(defn hicc-base [title {:keys [:flash-message :flash-alert-type :css :plain-js] :as opts} content]
  (html5 [:head
          [:title title]
          (include-css "/css/bootstrap.min.css")
          (include-css "/css/bootstrap-theme.min.css")
          (include-css "/css/screen.css")
          (when css (include-css css))

          [:body

           [:div#header.navbar.navbar-inverse.navbar-fixed-top {:role "navigation"}
            [:div.container-fluid
             [:div.navbar-header
              [:button.navbar-toggle {:type "button" :data-toggle "collapse" :data-target ".navbar-collapse"}
               [:span.sr-only "Toggle Navigation"]
               [:span.icon-bar]
               [:span.icon-bar]
               [:span.icon-bar]]
              [:a.navbar-brand {:href "/"}"{{name}}"]]

             [:div (menu opts)]]]

           [:div#wrap [:div#content
                       (when flash-message [:div#flash-message.alert {:class flash-alert-type} flash-message])
                       content]]

           [:div (footer)]

           (include-js "/js/jquery-2.0.3.min.js")
           (include-js "/js/bootstrap.min.js")
           (when plain-js [:div plain-js])]]))




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