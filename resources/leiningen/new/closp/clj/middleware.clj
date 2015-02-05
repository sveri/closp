(ns {{ns}}.middleware
  (:require [taoensso.timbre :as timbre]
    [selmer.middleware :refer [wrap-error-page]]
    [prone.middleware :refer [wrap-exceptions]]
    [noir-exception.core :refer [wrap-internal-error]]
    [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
    [buddy.auth.accessrules :refer [wrap-access-rules]]
    [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
    [de.sveri.clojure.commons.middleware.util :refer [wrap-trimmings]]
    [{{ns}}.service.auth :refer [auth-backend]]
    [{{ns}}.service.auth :as auth]
    [{{ns}}.globals :as glob]))

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
   #(wrap-internal-error % :log (fn [e] (timbre/error e)))
   wrap-anti-forgery
   wrap-trimmings])

(defn load-middleware []
  (concat production-middleware
          (when (= glob/env :dev) development-middleware)))
