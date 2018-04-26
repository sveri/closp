(ns {{ns}}.views.base
  (:require [hiccup.page :refer [html5 include-css include-js]]))


(defn hicc-base [title languages]
  (html5 [:head
          [:title title]
          (include-css "/css/bootstrap.min.css")
          (include-css "/css/main.css")]

         [:body
          [:input#languages {:type "hidden" :value languages}]
          [:div#app]

          (include-js "/js/jquery-3.3.1.min.js")
          (include-js "/js/bootstrap.min.js")
          (include-js "/js/compiled/app.js")
          [:div "<script type=\"text/javascript\">{{ns}}.core.init();</script>"]]))

(defn render [title {:keys [languages]}]
  (hicc-base title languages))
