(ns {{name}}.dev
  (:require [schema.core :as s]
            [{{ns}}.core :as core]))

(s/set-fn-validation! true)

(enable-console-print!)

(defn main [] (core/main))
