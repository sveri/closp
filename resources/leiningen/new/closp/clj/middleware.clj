(ns {{ns}}.middleware
  (:require [prone.middleware :as prone]
            [taoensso.tempura :refer [tr] :as tempura]
            [ring.middleware.reload :refer [wrap-reload]]
            [{{ns}}.locale :as loc]))

(defn add-locale [handler]
  (fn [req]
    (let [accept-language (get-in req [:headers "accept-language"])
          short-languages (or (tempura/parse-http-accept-header accept-language) ["en"])]
      (handler (assoc req :localize (partial tr
                                             {:default-locale :en
                                              :dict           loc/local-dict}
                                             short-languages)
                          :languages short-languages)))))

(defn add-req-properties [handler config]
  (fn [req] (handler (assoc req :config config))))

(def development-middleware
  [#(prone/wrap-exceptions % {:app-namespaces ['{{ns}}]})
   wrap-reload])

(defn production-middleware [config]
  [#(add-req-properties % config)
   add-locale])

(defn load-middleware [config]
  (concat (production-middleware config)
          (when (= (:env config) :dev) development-middleware)))
