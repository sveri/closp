(ns {{ns}}.closp-schema
  (:require [schema.core :as s #?@(:cljs [:include-macros true])
             ;:refer [defschema required-key Str]
             ]))

;make all keys optional

;(ns scrabble.common.utils.schema
;  (:require [schema-tools.core :as st]
;            [schema-tools.walk :as st-walk]))
;
;(defn with-optional-keys [schema]
;  (st-walk/walk schema
;                (fn [x]
;                  (let [y (with-optional-keys x)]
;                    (if (and (map? y) (not (record? y)))
;                      (st/optional-keys y)
;                      y)))
;                identity))

;(defmacro ds [& body] `(s/defschema ~@body))


;;;;;;;;; cljs schema for cc

(s/def data-types
  (s/enum "varchar" "boolean" "text"))

(s/def default-cljs-column
  {:name (s/eq "") :type (s/eq "varchar") :length (s/eq 100) :nullable s/Bool :id (s/eq 1)})

(s/def new-entity-definition
  {:cur-id  s/Num :name s/Str
   :columns [{:name s/Str :type data-types :length s/Num :id s/Num :nullable s/Bool}]})

(s/def existing-entities
  [{:name s/Str :content s/Str}])

(s/def cljs-state
  {:ex-entities existing-entities :new-entity new-entity-definition})


;;; clj schema for table definition ;;;;;;;;;;;

(def varchar [(s/one (s/eq :varchar) "varchar") (s/one s/Num "varchar-length")])
(def other-type (s/cond-pre (s/eq :text) (s/eq :time) (s/eq :int) (s/eq :boolean)))
(def options (s/cond-pre (s/eq :null) (s/eq :default) (s/eq :unique)))

(def table-column
  [(s/one s/Keyword "col-name")
   (s/one (s/cond-pre varchar other-type) "col-type")
   (s/optional options "opt1")
   (s/optional s/Any "opt1-type")
   (s/optional options "opt2")
   (s/optional s/Any "opt2-type")
   (s/optional options "opt3")
   (s/optional s/Any "opt3-type")
   ])

