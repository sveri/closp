(ns {{ns}}.service.cc
  (:require [schema.core :as s]
            [{{ns}}.closp-schema :as c-schem]))

(s/defn cljs-col->new-entity-col
        :- c-schem/cc-table-column
        [col :- c-schem/new-entity-column]
        (reduce-kv (fn [acc key value]
                     (cond
                       (>= (.indexOf [:name :type] key) 0) (conj acc (keyword value))
                       (= :length key) (if (= :varchar (second acc))
                                         (assoc acc 1 [:varchar value])
                                         acc)
                       (>= (.indexOf [:default :unique] key) 0) (conj acc key value)
                       (= :nullable key) (conj acc :null value)
                       :else acc))
                   [] col))

(s/defn cljs-new-entity->cc-entity
        :- c-schem/cc-entity-definiton
        [new-entity :- c-schem/cljs-new-entity-definition]
        {:name (:name new-entity) :columns (mapv cljs-col->new-entity-col (:columns new-entity))})