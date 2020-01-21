(ns {{ns}}.user
  (:require [{{ns}}.components.components :refer [dev-system]]
            [clojure.tools.namespace.repl :refer [refresh]]
            [system.repl :refer [system set-init! start stop reset]]))

(defn startup []
  (set-init! #'dev-system)
  (start))

(defn restart []
  (stop)
  (refresh)
  (start))