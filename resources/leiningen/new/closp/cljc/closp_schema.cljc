(ns {{ns}}.closp-schema
  (:require [schema.core :as s #?@(:cljs [:include-macros true])]
            [{{ns}}.closp-schema-helper :as schem-h]))


;;; clj schema for table definition ;;;;;;;;;;;

(def varchar [(s/one (s/eq :varchar) "varchar") (s/one s/Num "varchar-length")])
(def other-type (s/enum :text :time :int :boolean))

(def table-column-name-and-type
  [(s/one s/Keyword "col-name")
   (s/one (s/cond-pre varchar other-type) "col-type")
   s/Any])

(def cc-table-column (s/constrained table-column-name-and-type schem-h/table-column-pred))

(def cc-entity-definiton {:name s/Str :columns [cc-table-column]})
(def cc-entity-definitons [cc-entity-definiton])



;; helper functions
(defn wrap-with-response [body] {:status (s/eq 200) :headers {} :body body})




;;;;;;;;; cljs schema for cc

(s/def data-types
  (s/enum "varchar" "boolean" "text"))

(s/def default-cljs-column
  [(s/one (s/eq :empty-name) "empty name")
   (s/one varchar "varchar")
   (s/one (s/eq :null) "nullable")
   (s/one (s/eq false) "null-false")])

(def cljs-new-entity {:name s/Str :columns [default-cljs-column]})

(s/def cljs-state {:ex-entities cc-entity-definitons :new-entity cc-entity-definiton})

