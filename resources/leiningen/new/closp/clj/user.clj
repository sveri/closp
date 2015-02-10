(ns {{ns}}.user
  (:require [reloaded.repl :refer [system init start stop go reset]]
            [{{ns}}.dev :refer [start-figwheel]]
            [{{ns}}.components.components :refer [dev-system]]))

(start-figwheel)

(reloaded.repl/set-init! dev-system)
