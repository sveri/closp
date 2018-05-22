(ns {{ns}}.views
  (:require [{{ns}}.subs :as subs]
            [{{ns}}.common :as comm]
            [{{ns}}.cljc.locale :as loc]
            [{{ns}}.routes :as routes]
            [{{ns}}.helper :refer [>evt <sub]]
            [{{ns}}.user.views :as uv]
            [{{ns}}.user.helper :as u-h]
            [{{ns}}.user.subs :as u-subs]
            [{{ns}}.user.events :as u-ev]
            [{{ns}}.home.views :as hv]))


(defn menu []
      (let [user (<sub [::u-subs/user])]
           [:nav.navbar.fixed-top.navbar-expand-lg.navbar-light.bg-light
            [:a.navbar-brand {:href "/"} "{{name}}"]
            [:button.navbar-toggler
             {:type          "button", :data-toggle "collapse",
              :data-target   "#navbarTop", :aria-controls "navbarTop",
              :aria-expanded "false", :aria-label "Toggle navigation"}
             [:span {:class "navbar-toggler-icon"}]]
            [:div#navbarTop.collapse.navbar-collapse
             [:ul.navbar-nav.w-100

              (if (u-h/user-is-logged-in? user)
                [:li.nav-item.dropdown.ml-auto
                 [:a#profileDropdownLink.nav-link.dropdown-toggle {:href "#", :data-toggle "dropdown", :aria-haspopup "true", :aria-expanded "false"}
                  (u-h/get-displayname user)]
                 [:div.dropdown-menu.dropdown-menu-right {:aria-labelledby "profileDropdownLink"}
                  [:a.dropdown-item {:href (routes/url-for :changepassword)} (loc/localize [:user/change_password])]
                  [:button.btn.dropdown-item {:on-click #(>evt [::u-ev/logout])} (loc/localize [:generic/logout])]]]

                [:div.float-right.ml-auto [:a.btn.btn-primary {:href (routes/url-for :login)} (loc/localize [:user/signin])]])]]]))

(defn footer []
      [:footer.footer
       [:div.container
        [:div.row
         [:div.col
          [:a {:href (routes/url-for :contact)} (loc/localize [:generic/contact])]]
         [:div.col
          [:a {:href (routes/url-for :tos)} (loc/localize [:generic/tos])]]
         [:div.col
          [:a {:href (routes/url-for :cookies)} (loc/localize [:generic/cookies])]]]]])


(defn base-view [content]
      (let [show-loading-screen (<sub [::comm/loading-screen])]
           [:div
            (if show-loading-screen
              [:div#loading-screen
               [:p [:img {:src "/img/loading.gif"}]]]

              [:div
               [:header [menu]]

               [:main
                [:div#content

                 (when (<sub [::comm/generic-error-sub])
                       [:div.alert.alert-danger.alert-dismissible.fade.show {:role "alert"}
                        (<sub [::comm/generic-error-sub])
                        [:button.close {:type "button" :aria-label "Close" :onClick #(>evt [::comm/clear-generic-error])}
                         [:span {:aria-hidden "true"} "x"]]])

                 (when (<sub [::comm/generic-success-sub])
                       [:div.alert.alert-success.alert-dismissible.fade.show {:role "alert"}
                        (<sub [::comm/generic-success-sub])
                        [:button.close {:type "button" :aria-label "Close" :onClick #(>evt [::comm/clear-generic-success])}
                         [:span {:aria-hidden "true"} "x"]]])

                 [content]]]

               [footer]])]))



(defn default-route []
      (fn []
          [:div
           [:h1 "Default Route"]
           [:div [:a {:href (routes/url-for :home)} "go to Home Page"]]]))


(defn main-panel []
      (condp = (<sub [::subs/active-panel])
             :home (base-view hv/home-panel)
             :login (base-view uv/login-panel)
             :signup (base-view uv/signup-panel)
             :changepassword (base-view uv/change-password-panel)
             (base-view default-route)))

