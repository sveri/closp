(ns {{ns}}.core
  (:require [taoensso.timbre :as timbre]
            [reloaded.repl :refer [go]]
            [{{ns}}.cljxcore :as cljx]
            [{{ns}}.components.components :refer [prod-system]])
  (:gen-class))

(defn parse-port [args]
  (if-let [port (->> args (remove #{"-dev"}) first)]
    (Integer/parseInt port)
    3000))

(defn -main [& args]
  (let [port (parse-port args)]
    (reloaded.repl/set-init! prod-system)
    (go)
    (cljx/foo-cljx "hello from cljx")
    (timbre/info "server started on port:" port)))
