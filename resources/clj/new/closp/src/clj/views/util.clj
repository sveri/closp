(ns {{namespace}}.views.util
  (:require [hiccup.page :as hp]
            [ring.middleware.anti-forgery :as af]))

(defn af-token []
  [:input {:name "__anti-forgery-token", :type "hidden", :value af/*anti-forgery-token* :id "af-token-field"}])

(defn head [title]
  [:head [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
   [:title title]])

(defn responsive-app-bar [{:keys [localize] :as req}]
  (let [displayname (get-in req [:session :user :displayname])
        role (get-in req [:session :user :role])]
    [:header
     [:ul#navbar-profile-dropdown.dropdown-content
      (when (= "admin" role) [:li [:a {:href "/admin/users"} (localize [:user/users])]])
      [:li [:a {:href "/user/changepassword"} (localize [:user/change_password])]]
      [:li.divider]
      [:li [:a {:href "/user/logout"} (localize [:generic/logout])]]]
     [:nav
      [:div.nav-wrapper
       [:a {:href "/", :class "brand-logo"} "{{name}}"]
       [:a {:href "#", :data-target "side-drawer", :class "sidenav-trigger"}
        [:i.material-icons "menu"]]

       [:ul.right.hide-on-med-and-down
        [:li [:a {:href "/reagent-example"} "Reagent Example"]]
        (when displayname
          [:span
           [:li [:a.dropdown-trigger {:href "#!", :data-target "navbar-profile-dropdown"} displayname
                 [:i.material-icons.right "arrow_drop_down"]]]])
        (when-not displayname
          [:span
           [:li [:a.waves-effect.waves-light.btn {:href "/user/login"} (localize [:user/signin])]]
           [:li [:a.waves-effect.waves-light.btn {:href "/user/signup"} (localize [:user/register])]]])]]]
     [:ul#side-drawer.sidenav
      [:li [:a {:href "/reagent-example"} "Reagent Example"]]
      [:li.divider]
      (when displayname
        [:span
         (when (= "admin" role) [:li [:a {:href "/admin/users"} (localize [:user/users])]])
         [:li [:a {:href "/user/changepassword"} (localize [:user/change_password])]]
         [:li [:a {:href "/user/logout"} (localize [:generic/logout])]]])
      (when-not displayname
        [:span
         [:li [:a {:href "/user/login"} (localize [:user/signin])]]
         [:li [:a {:href "/user/signup"} (localize [:user/register])]]])]]))



(defn footer [{:keys [localize]}]
  [:footer.page-footer
   [:div.footer-copyright
    [:div.container "© 2020 Copyright Text Closp"
     [:div.right
      [:a.grey-text.text-lighten-4.margin-right-10 {:href "/contact"} (localize [:contact/contact])]
      [:a.grey-text.text-lighten-4.margin-right-10 {:href "/tos"} (localize [:generic/tos])]
      [:a.grey-text.text-lighten-4 {:href "/cookies"} (localize [:generic/cookies])]]]]])

(defn render-content-with
  ([req title content] (render-content-with req title content nil))
  ([req title content toast]
   (hp/html5
     (hp/include-css "/css/material-icons.css")
     (hp/include-css "/css/materialize.min.css")
     (hp/include-css "/css/screen.css")
     (head title)
     (responsive-app-bar req)
     [:main [:div.content content]]
     (footer req)
     (hp/include-js "/js/materialize.min.js")
     (hp/include-js "/js/material.init.js")
     (when-let [toast (get-in req [:flash :toast])]
       [:script {:type (str "text/javascript")} "M.toast({html: '" (:text toast)"',  classes: '" (:classes toast)"'});
     "]))))
