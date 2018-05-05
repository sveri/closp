(ns {{ns}}.routes.util
  (:require [ring.util.http-response :as resp]))

(defmacro with-try [& args]
  (try
    `(do ~@args)
    (catch Exception e (do (.printStackTrace e)
                           (resp/internal-server-error)))))
