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


(s/def data-types (s/enum "Varchar" "Boolean" "Text"))
(s/def html-checkbox (s/enum "on" "off"))

(s/def default-cljs-column {:name (s/eq "") :type (s/eq "Varchar") :length (s/eq 100) :nullable s/Bool :id (s/eq 1)})

(s/def new-entity-definition {:cur-id  s/Num :name s/Str
                              :columns [{:name s/Str :type data-types :length s/Num :id s/Num :nullable s/Bool}]})

(s/def existing-entities [{:name s/Str :content s/Str}])

(s/def cljs-state
  {:ex-entities existing-entities :new-entity new-entity-definition})

