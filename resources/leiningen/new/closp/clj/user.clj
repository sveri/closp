(ns {{ns}}.user
  (:require [reloaded.repl :refer [go]]
            [{{ns}}.components.components :refer [dev-system]]))

(defn start-dev-system []
  (go))

(reloaded.repl/set-init! dev-system)
