(ns {{ns}}.views.base
  (:require [ring.middleware.anti-forgery :as af]
            [hiccup.page :refer [html5 include-css include-js]]
            [clojure.string :as str]
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

(defn merge-active
  ([page expected-page]
   (merge-active page expected-page ""))
  ([page expected-page css]
   (let [active-css (if (str/includes? page expected-page) "active" "")]
     (str css " " active-css))))



(defn menu [{:keys [uri role nexturl identity registration-allowed? localize]}]
  [:nav {:class "navbar navbar-expand-md navbar-dark bg-dark"}
   [:a {:class "navbar-brand", :href "/"} (localize [:generic/home])]
   [:button {:class "navbar-toggler collapsed", :type "button", :data-toggle "collapse", :data-target "#topMenu", :aria-controls "topMenu", :aria-expanded "false", :aria-label "Toggle navigation"}
    [:span {:class "navbar-toggler-icon"}]]



   [:div {:class "navbar-collapse collapse", :id "topMenu"}
    [:ul {:class "navbar-nav mr-auto"}

     [:li {:class (merge-active uri "/reagent-example" "nav-item")}
      [:a {:class "nav-link", :href "/reagent-example"} "ReFrame Example"]]

     (if identity
       [:li {:class (merge-active uri "/" "nav-item")}
        [:a {:class "nav-link", :href "/"} "LoggedIn Home"]]

       (when (= role "admin")
         [:li {:class (merge-active uri "admin" "nav-item dropdown")}
          [:a {:class "nav-link dropdown-toggle", :href "#", :id "admin-dropdown-menu", :data-toggle "dropdown",
               :aria-haspopup "true", :aria-expanded "false"} "Admin"]
          [:div {:class "dropdown-menu", :aria-labelledby "admin-dropdown-menu"}
           [:a {:class "dropdown-item", :href "/admin/users"} "Users"]]]))]]

   (if identity
     [:ul {:class "navbar-nav ml-auto"}
      [:li {:class (merge-active uri "user" "nav-item dropdown")}
       [:a {:class "nav-link dropdown-toggle", :href "#", :id "user-dropdown-menu", :data-toggle "dropdown",
                                                :aria-haspopup "true", :aria-expanded "false"} identity]
       [:div {:class "dropdown-menu", :aria-labelledby "user-dropdown-menu"}
        [:a {:class "dropdown-item", :href "/user/changepassword"} (localize [:user/change_password])]
        [:a {:class "dropdown-item", :href "/user/logout"} (localize [:generic/logout])]]]]

     [:form {:class "form-inline mt-2 mt-md-0", :action "/user/login", :method "POST"}
      (af-token)
      (when nexturl [:input {:name "nexturl", :type "hidden", :value nexturl}])
      [:input {:class "form-control mr-sm-2", :id "upper_email", :type "text", :placeholder (localize [:generic/email]), :name "username"}]
      [:input {:class "form-control mr-sm-2", :id "upper_password", :type "password", :placeholder (localize [:generic/password]), :name "password"}]
      [:button {:class "btn btn-outline-primary my-2 my-sm-0", :type "submit"} (localize [:user/signin])]
      (when registration-allowed?
        [:a {:href "/user/signup", :class "btn btn-success"} (localize [:user/signup])])])])

(defn footer [uri localize]
  [:footer
   [:div {:class "container"}
    [:ul {:class "nav"}
     [:li {:class "nav-item"}
      [:a {:class (merge-active uri "contact " "nav-link"), :href "/contact"} (localize [:generic/contact])]]]]])


(defn hicc-base [title {:keys [flash-message flash-alert-type css plain-js uri localize] :as opts} content]
  (html5 [:head
          [:title title]
          (include-css "/css/bootstrap.min.css")
          (include-css "/css/screen.css")
          (when css (include-css css))]

         [:body.d-flex.flex-column
          [:header (menu opts)]

          [:main.container-fluid.flex-grow-1 {:role "main"}
           [:div (when flash-message [:div#flash-message.alert {:class flash-alert-type} flash-message])
            content]]

          [:footer (footer uri localize)]

          (include-js "/js/jquery.min.js")
          (include-js "/js/bootstrap.min.js")
          (when plain-js [:div plain-js])]))




(defn render [title {:keys [localize config uri css plain-js nexturl] :as req} content]
  (hicc-base title
             {:localize         localize :identity (sess/get :identity)
              :role             (sess/get :role) :registration-allowed? (:registration-allowed? config)
              :captcha-enabled? (:captcha-enabled? config)
              :flash-message    (sess/flash-get :flash-message)
              :flash-alert-type (sess/flash-get :flash-alert-type)
              :uri              uri :css css :plain-js plain-js
              :nexturl          (get (get req :query-params {}) "nexturl" "/")}
             content))
