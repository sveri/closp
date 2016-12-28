(ns {{ns}}.middleware
  (:require [clojure.tools.logging :as log]
            [prone.middleware :as prone]
            [taoensso.tempura :refer [tr]]
            [noir-exception.core :refer [wrap-internal-error]]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [buddy.auth.accessrules :refer [wrap-access-rules]]
            [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
            [noir.session :as sess]
            [de.sveri.clojure.commons.middleware.util :refer [wrap-trimmings]]
            [clojure-miniprofiler :refer [wrap-miniprofiler in-memory-store]]
            [ring.middleware.transit :refer [wrap-transit-response]]
            [ring.middleware.reload :refer [wrap-reload]]
            [{{ns}}.locale :as loc]
            [{{ns}}.service.auth :refer [auth-backend]]
            [{{ns}}.service.auth :as auth]))

(defonce in-memory-store-instance (in-memory-store))

(defn log-request [handler]
  (fn [req]
    (log/debug req)
    (handler req)))

(defn add-locale [handler]
  (fn [req]
    (let [accept-language (get-in req [:headers "accept-language"])
          short-lang (cond
                       (.contains accept-language "de") "de"
                       :else "en")]
      (sess/put! :locale short-lang)
      (handler (assoc req :localize (partial tr
                                             {:default-locale :en
                                              :dict           loc/local-dict}
                                             [(keyword short-lang)]))))))

(defn add-req-properties [handler config]
  (fn [req]
    (sess/put! :registration-allowed? (:registration-allowed? config))
    (sess/put! :captcha-enabled? (:captcha-enabled? config))
    (handler req)))

(def development-middleware
  [#(wrap-miniprofiler % {:store in-memory-store-instance})
   #(prone/wrap-exceptions % {:app-namespaces ['{{ns}}]})
   wrap-reload])

(defn production-middleware [config tconfig]
  [#(add-req-properties % config)
   add-locale
   #(wrap-access-rules % {:rules auth/rules})
   #(wrap-authorization % auth/auth-backend)
   #(wrap-internal-error % :log (fn [e] (log/error e)))
   #(wrap-transit-response % {:encoding :json :opts {}})
   wrap-anti-forgery
   wrap-trimmings])

(defn load-middleware [config tconfig]
  (concat (production-middleware config tconfig)
          (when (= (:env config) :dev) development-middleware)))