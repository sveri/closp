(ns {{ns}}.db.entities
  (:require [clojure.spec :as s]
            [{{ns}}.service.spec-utils :as su]
            [clojure.java.jdbc :as j]
            [clojure.string :as str]
            [clojure.edn :as edn]
            [clj-time.coerce :as t-coerce]
            [clojure.instant :as inst])
  (:import (java.sql Timestamp)))




(s/def ::table-name ::su/non-empty-string)
(s/def ::to-string (s/coll-of ::su/non-empty-string))

(s/def ::default-order-field ::su/non-empty-string)
(s/def ::default-order #{"asc" "desc"})

(s/def ::name ::su/non-empty-string)
(s/def ::type keyword?)
(s/def ::max-length integer?)
(s/def ::not-null boolean?)

(s/def ::field (s/keys :req-un [::name ::type]
                       :opt-un [::max-length ::not-null]))

(s/def ::fields (s/coll-of ::field))

(s/def ::entity (s/keys :req-un [::table-name ::to-string ::fields]
                        :opt-un [::default-order-field ::default-order]))

(s/def ::entities (s/coll-of ::entity))


(s/def ::not-null #(not (nil? %)))
(s/def ::not-empty #(not (str/blank? %)))
(s/def ::string? string?)
(s/def ::number? number?)
(s/def ::integer? integer?)
(s/def ::date? inst?)
(s/def ::max-30 #(<= (count %) 30))
(s/def ::max-100 #(<= (count %) 100))
(s/def ::max-200 #(<= (count %) 200))



(def number-conformer (s/conformer edn/read-string))
;(def sql-date-transformer (s/conformer #(new Timestamp (.getTime (inst/read-instant-date %)))))

(def entities
  {:user {:table-name          "users"
          :default-order-field "email"
          :default-order       "asc"
          :to-string           [:email]
          :fields              [{:name "email" :type :string :max-length 30 :spec (s/and ::string? ::max-30 ::not-null)}
                                {:name "pass" :type :string :max-length 200 :spec (s/and ::string? ::max-200 ::not-null)}
                                {:name "first_name" :type :string :max-length 30 :spec (s/and ::string? ::max-30)}
                                {:name "last_name" :type :string :max-length 30 :spec (s/and ::string? ::max-30)}
                                {:name "role" :type :string :spec (s/and ::string? ::max-30 ::not-null)}
                                {:name "last_login" :type :time :spec inst?}
                                {:name "is_active" :type :boolean :spec boolean? :not-null true}]}

   :team {:table-name          "team"
          :default-order-field "name"
          :default-order       "asc"
          :to-string           [:name]
          :fields              [{:name "name" :type :string :spec (s/and ::string? ::not-empty ::max-100)}
                                {:name "owner_id" :type :fk :fk-ref :user :fk-ref-column :email :spec (s/and ::not-null ::integer?)
                                 :conformer number-conformer}]}})








(defn get-by-id [db entity id]
  (first (j/query db [(format "select * from %s where id = ?" (:table-name entity)) id])))



(defn get-all [db entity]
  (let [default-order-field (:default-order-field entity)
        default-order (:default-order entity)
        sel-str (str " select * from " (:table-name entity)
                  (if default-order-field
                    (str " order by " default-order-field (if default-order (str " " default-order) " asc "))
                    " "))]
    (j/query db [sel-str])))



(defn read-field [s field]
  (if-not (= :string (:type field)) (edn/read-string s) s))

(defn add-form-data-to-insert-map [m req field]
  (let [param-data (-> req (get-in [:form-params (:name field)]))]
    (cond
      (str/blank? param-data) {}
      :else (assoc m (keyword (:name field)) (read-field param-data field)))))


(defn create [db entity req]
  (let [table-name (:table-name entity)
        fields (:fields entity)
        insert-map (reduce
                     (fn [m field]
                       (add-form-data-to-insert-map m req field))
                     {}
                     fields)]
    (j/insert! db table-name insert-map)))


(defn edit [db entity req]
  (let [table-name (:table-name entity)
        fields (:fields entity)
        insert-map (reduce
                     (fn [m field]
                       (add-form-data-to-insert-map m req field))
                     {}
                     fields)]
    (j/update! db table-name insert-map ["id = ?" (edn/read-string (get-in req [:form-params "id"]))])))


(s/fdef delete :args (s/cat :id integer? :entity (s/or :keyword keyword? :entity ::entity)
                            :where (s/cat :prepared-stmt ::string? :inserts (s/+ any?))))
(defn delete [db entity where]
  (j/delete! db (keyword (:table-name entity)) where))
