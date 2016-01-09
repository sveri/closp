(ns {{ns}}.cc.common
  (:require [schema.core :as s :include-macros true]
            [reagent.core :as r]
            [{{ns}}.closp-schema :as schem]))

;(def varchar [(s/one (s/eq :varchar) "varchar") (s/one s/Num "varchar-length")])
;(s/def default-cljs-column
;  [
;   (s/one (s/eq "") "empty name")
;   (s/one varchar "varchar")
;   (s/one (s/eq :null) "nullable")
;   (s/one (s/eq false) "null-false")
;   ])

(def default-column
  (s/validate schem/default-cljs-column
              [:empty-name [:varchar 100] :null false]))
;
;;(.log js/console default-column)
;
(def new-entity-definition
  (s/validate schem/cljs-new-entity
              {:name ""
               :columns [default-column]}
              )
  )

;(def default-column
;  ["" [:varchar 100] :null false])

;(.log js/console default-column)

;(def new-entity-definition{:name ""
;               :columns [default-column]})

(def state (r/atom {:ex-entities [] :new-entity new-entity-definition}))
;
;(add-watch state :validate (fn [_ _ _ n] (s/validate schem/cljs-state n)))
