(ns {{ns}}.user
  (:require [{{ns}}.components.components :refer [dev-system]]
            [system.repl :refer [system set-init! start stop reset]]))

(defn startup []
  (set-init! #'dev-system)
  (start))