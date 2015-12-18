(ns {{ns}}.core
  (:require [taoensso.timbre :as timbre]
            [reloaded.repl :refer [go]]
            [mount.core :as mount]
            [{{ns}}.cljccore :as cljc]
            [{{ns}}.components.server])
  (:gen-class))

(defn -main [& args]
  (mount/start)
  (cljc/foo-cljc "hello from cljx")
  (timbre/info "server started."))
