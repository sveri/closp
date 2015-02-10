(ns {{ns}}.dev
  (:require [cemerick.piggieback]
            [weasel.repl.websocket]
            [leiningen.core.main :as lein]))

(defn start-figwheel []
  (future
    (print "Starting figwheel.\n")
    (lein/-main ["figwheel"])))
