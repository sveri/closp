(ns user
  (:require [system.repl :refer [system set-init! start stop reset]]
            [{{namespace}}.components.components :refer [dev-system]]))

(set-init! #'dev-system)
