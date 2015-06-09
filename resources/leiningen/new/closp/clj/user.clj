(ns {{ns}}.user
  (:require [reloaded.repl :refer [go reset]]
            [{{ns}}.components.components :refer [dev-system]]))

(defn start-dev-system []
  (go))

(reloaded.repl/set-init! dev-system)
