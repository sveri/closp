(ns {{ns}}.dev
  (:require [cemerick.piggieback]
            [leiningen.core.main :as lein]))

(defn start-figwheel []
  (future
    (print "Starting figwheel.\n")
    (lein/-main ["figwheel"])))
