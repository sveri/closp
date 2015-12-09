(ns {{ns}}.user
  (:require [reloaded.repl :refer [go reset stop]]
            [schema.core :as s]
            [{{ns}}.components.components :refer [dev-system]]))

(defn start-dev-system []
  (s/set-fn-validation! true)
  (go))

(reloaded.repl/set-init! dev-system)
