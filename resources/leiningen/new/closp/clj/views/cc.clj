(ns de.sveri.gup.views.cc
  (:require [de.sveri.gup.views.base :as v]))


(defn index-page [req]
  (v/render
    "" (merge req {:plain-js "<script type=\"text/javascript\">de.sveri.gup.cc.core.main();</script>"})
    [:div
     [:div#app]]))
