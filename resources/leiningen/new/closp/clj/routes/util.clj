(ns {{ns}}.routes.util
  (:require [ring.util.http-response :as resp]))

(defmacro with-try [& body]
  (try
    `(do ~@body)
    (catch Exception e (do (.printStackTrace e)
                           (resp/internal-server-error)))))
