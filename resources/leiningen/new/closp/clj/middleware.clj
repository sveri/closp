(ns {{ns}}.middleware
  (:require [clojure.tools.logging :as log]
            [prone.middleware :as prone]
            [taoensso.tempura :refer [tr] :as tempura]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [buddy.auth.accessrules :refer [wrap-access-rules]]
            [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
            [ring.middleware.transit :refer [wrap-transit-response]]
            [ring.middleware.reload :refer [wrap-reload]]
            [clojure.string :as s]
            [clojure.walk :refer [prewalk]]
            [{{ns}}.locale :as loc]
            [{{ns}}.service.auth :refer [auth-backend]]
            [{{ns}}.service.auth :as auth]))


(def trim-param-list [:params :form-params :edn-params])

(defn- trim-params [req p-list]
  (if (= :post (:request-method req))
    (let [prewalk-trim #(if (string? %) (s/trim %) %)]
      (reduce (fn [m k] (assoc m k (prewalk prewalk-trim (get-in req [k])))) req p-list))
    req))

(defn wrap-trimmings
  "string/trim every parameter in :params or :form-params"
  [handler]
  (fn [req] (handler (trim-params req trim-param-list))))

(defn add-locale [handler]
  (fn [req]
    (let [accept-language (get-in req [:headers "accept-language"])
          short-languages (or (tempura/parse-http-accept-header accept-language) ["en"])]
      (handler (assoc req :localize (partial tr
                                             {:default-locale :en
                                              :dict           loc/local-dict}
                                             short-languages))))))

(defn add-req-properties [handler config]
  (fn [req] (handler (assoc req :config config))))

(def development-middleware
  [#(prone/wrap-exceptions % {:app-namespaces ['{{ns}}]})
   wrap-reload])

(defn production-middleware [config]
  [#(add-req-properties % config)
   add-locale
   #(wrap-access-rules % {:rules auth/rules})
   #(wrap-authorization % auth/auth-backend)
   #(wrap-transit-response % {:encoding :json :opts {}})
   wrap-anti-forgery
   wrap-trimmings])

(defn load-middleware [config]
  (concat (production-middleware config)
          (when (= (:env config) :dev) development-middleware)))