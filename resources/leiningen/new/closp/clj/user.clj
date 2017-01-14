(ns {{ns}}.user
  (:require [system.repl :refer [set-init! start reset]]
            [{{ns}}.components.components :refer [dev-system]]))

(defn start-dev-system []
  (start))

(set-init! #'dev-system)
