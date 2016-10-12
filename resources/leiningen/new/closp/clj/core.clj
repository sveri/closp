(ns {{ns}}.core
  (:require [clojure.tools.logging :as log]
            [reloaded.repl :refer [go]]
            [{{ns}}.cljccore :as cljc]
            [{{ns}}.components.components :refer [prod-system]])
  (:gen-class))

(defn -main [& args]
  (reloaded.repl/set-init! prod-system)
  (go)
  (cljc/foo-cljc "hello from cljx")
  (log/info "server started."))
