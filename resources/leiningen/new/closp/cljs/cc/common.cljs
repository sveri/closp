(ns {{ns}}.cc.common
  (:require [schema.core :as s :include-macros true]
            [reagent.core :as r]
            [{{ns}}.closp-schema :as schem]))


(def default-column
  (s/validate schem/default-cljs-column [:empty-name [:varchar 100] :null false]))

(def new-entity-definition
  (s/validate schem/cljs-new-entity {:name "" :columns [default-column]}))


(def state (r/atom {:ex-entities [] :new-entity new-entity-definition}))
(add-watch state :validate (fn [_ _ _ n] (s/validate schem/cljs-state n)))
