(ns {{ns}}.user
  (:require [reloaded.repl :refer [system init start stop go reset]]
            [{{ns}}.dev :refer [start-figwheel]]
            [{{ns}}.components.components :refer [dev-system]]))



(defn start-dev-system []
  (start-figwheel)
  (go))

(reloaded.repl/set-init! dev-system)
