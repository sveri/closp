(ns {{ns}}.user
  (:require [system.repl :as sr]
            [clojure.tools.namespace.repl :as tn]
            [{{ns}}.components.components :refer [dev-system]]))

(defn start-dev-system []
  (sr/start))

(defn reset []
  (tn/refresh)
  (sr/reset))

(sr/set-init! #'dev-system)