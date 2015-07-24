(ns {{ns}}.middleware
  (:require [taoensso.timbre :as timbre]
    [selmer.middleware :refer [wrap-error-page]]
    [prone.middleware :refer [wrap-exceptions]]
    [noir-exception.core :refer [wrap-internal-error]]
    [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
    [buddy.auth.accessrules :refer [wrap-access-rules]]
    [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
    [noir.session :as sess]
    [de.sveri.clojure.commons.middleware.util :refer [wrap-trimmings]]
    [clojure-miniprofiler :refer [wrap-miniprofiler in-memory-store]]
    [{{ns}}.service.auth :refer [auth-backend]]
    [{{ns}}.service.auth :as auth]))

(defonce in-memory-store-instance (in-memory-store))

(defn log-request [handler]
  (fn [req]
    (timbre/debug req)
    (handler req)))

(defn add-req-properties [handler config]
  (fn [req]
    (sess/put! :registration-allowed? (:registration-allowed? config))
    (sess/put! :captcha-enabled? (:captcha-enabled? config))
    (handler req)))

(def development-middleware
  [wrap-error-page
   wrap-exceptions
   #(wrap-miniprofiler % {:store in-memory-store-instance})])

(defn production-middleware [config]
  [#(add-req-properties % config)
   #(wrap-access-rules % {:rules auth/rules })
   #(wrap-authorization % auth/auth-backend)
   #(wrap-internal-error % :log (fn [e] (timbre/error e)))
   wrap-anti-forgery
   wrap-trimmings])

(defn load-middleware [config]
  (concat (production-middleware config)
          (when (= (:env config) :dev) development-middleware)))