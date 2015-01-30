(ns {{ns}}.middleware
  (:require [taoensso.timbre :as timbre]
    [environ.core :refer [env]]
    [selmer.middleware :refer [wrap-error-page]]
    [prone.middleware :refer [wrap-exceptions]]
    [noir-exception.core :refer [wrap-internal-error]]
    [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
    [buddy.auth.accessrules :refer [wrap-access-rules]]
    [{{ns}}.service.auth :refer [auth-backend]]
    [{{ns}}.service.auth :as auth]))

(defn log-request [handler]
  (fn [req]
    (timbre/debug req)
    (handler req)))

(def development-middleware
  [wrap-error-page
   wrap-exceptions])

(def production-middleware
  [#(wrap-access-rules % {:rules auth/rules })
   #(wrap-authorization % auth/auth-backend)
   #(wrap-internal-error % :log (fn [e] (timbre/error e)))])

(defn load-middleware []
  (concat production-middleware
          (when (env :dev) development-middleware)))
