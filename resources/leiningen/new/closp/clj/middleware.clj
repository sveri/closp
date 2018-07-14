(ns {{ns}}.middleware
  (:require [prone.middleware :as prone]
            [taoensso.tempura :refer [tr] :as tempura]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.cors :refer [wrap-cors]]
            [{{ns}}.locale :as loc]
            [{{ns}}.db.user :as db-u]
            [{{ns}}.service.user :as s-u]))

(defn add-locale [handler]
  (fn [req]
    (let [accept-language (get-in req [:headers "accept-language"])
          short-languages (or (tempura/parse-http-accept-header accept-language) ["en"])]
      (handler (assoc req :localize (partial tr
                                             {:default-locale :en
                                              :dict           loc/local-dict}
                                             short-languages)
                          :languages short-languages)))))


(defn add-user [handler db]
  (fn [req]
    (let [user-id (s-u/get-user-id-from-req req)]
      (if (and user-id (nil? (:user req)))
        (handler (assoc req :user (dissoc (db-u/get-user-by-email db user-id) :pass)))
        (handler req)))))


(defn add-req-properties [handler config]
  (fn [req] (handler (assoc req :config config))))

(def development-middleware
  [#(prone/wrap-exceptions % {:app-namespaces ['{{ns}}]})
   #(wrap-cors %
               :access-control-allow-origin [#".*"]
               :access-control-allow-methods [:get :put :post :delete :options])
   wrap-reload])

(defn production-middleware [config]
  [#(add-req-properties % config)
   add-locale])

(defn load-middleware [config]
  (concat (production-middleware config)
          (when (= (:env config) :dev) development-middleware)))
