(ns {{ns}}.user
  (:require [clojure.tools.namespace.repl :as tn]
            [schema.core :as s]
            [mount.core :as mount]
            [{{ns}}.components.server]
            [{{ns}}.components.db]))

(defn start []
  (s/set-fn-validation! true)
  (mount/start))

(defn stop []
  (mount/suspend)
  (mount/stop))

(defn reset []
  (stop)
  (tn/refresh :after '{{ns}}.user/start))
