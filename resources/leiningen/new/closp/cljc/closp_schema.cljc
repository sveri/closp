(ns {{ns}}.closp-schema
  (:require [schema.core :as s #?@(:cljs [:include-macros true])]
            [{{ns}}.closp-schema-helper :as schem-h]))


;;; clj schema for table definition ;;;;;;;;;;;

(def varchar [(s/one (s/eq :varchar) "varchar") (s/one s/Num "varchar-length")])
(def other-type (s/cond-pre (s/eq :text) (s/eq :time) (s/eq :int) (s/eq :boolean)))

(def table-column-name-and-type
  [(s/one s/Keyword "col-name")
   (s/one (s/cond-pre varchar other-type) "col-type")
   s/Any
   ])

(def cc-table-column
  (s/constrained table-column-name-and-type schem-h/table-column-pred))

(def cc-entity-definiton {:name s/Str :columns [cc-table-column]})



;; helper functions
(defn wrap-with-response [body]
  {:status (s/eq 200) :headers {} :body body})




;;;;;;;;; cljs schema for cc

(s/def data-types
  (s/enum "varchar" "boolean" "text"))

(s/def default-cljs-column
  [(s/one (s/eq :empty-name) "empty name")
   (s/one varchar "varchar")
   (s/one (s/eq :null) "nullable")
   (s/one (s/eq false) "null-false")])
;
;(def cljs-new-entity {:name s/Str :columns s/Any})
(def cljs-new-entity {:name s/Str :columns [default-cljs-column]})

(s/def new-entity-column {:name s/Str :type data-types :length s/Num :id s/Num :nullable s/Bool})

(s/def cljs-new-entity-definition
  {:name s/Str
   :columns [new-entity-column]})

(s/def existing-entity
  {:name s/Str :content s/Str})

(s/def existing-entities
  [existing-entity])

(s/def cljs-state (s/atom {:ex-entities existing-entities :new-entity cc-entity-definiton}))


;;;; old version

;
;(s/def data-types
;  (s/enum "varchar" "boolean" "text"))
;
;(s/def default-cljs-column
;  {:name (s/eq "") :type (s/eq "varchar") :length (s/eq 100) :nullable s/Bool :id (s/eq 1)})
;
;(s/def new-entity-column {:name s/Str :type data-types :length s/Num :id s/Num :nullable s/Bool})
;
;(s/def cljs-new-entity-definition
;  {:cur-id  s/Num :name s/Str
;   :columns [new-entity-column]})
;
;(s/def existing-entity
;  {:name s/Str :content s/Str})
;
;(s/def existing-entities
;  [existing-entity])
;
;(s/def cljs-state
;  {:ex-entities existing-entities :new-entity cljs-new-entity-definition})