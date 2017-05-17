(ns {{ns}}.crudify.validation-test
  (:require [clojure.test :refer :all]
            [taoensso.tempura :refer [tr]]
            [clojure.spec.alpha :as s]
            [{{ns}}.crudify.validation :as val]
            [{{ns}}.db.entities :as ent]
            [{{ns}}.components.locale :as l]
            [{{ns}}.crudify.util :as u]))

(def t (partial tr
                {:default-locale :en
                 :dict           l/local-dict}
                ["en"]))


(def email-field {:name "email" :type :string :max-length 30 :spec (s/and ::ent/string? ::ent/max-30 ::ent/not-empty)})
(def number-field {:name "age" :type :string :max-length 30 :spec (s/and ::ent/number? ::ent/max-100 ::ent/not-null)})
(def date-field {:name "date" :type :date :max-length 30 :spec (s/and ::ent/date? ::ent/not-null) :conformer ent/sql-date-transformer})

;(deftest notempty
;  (is (= (t [:crudify/not-empty] [(u/prepare-str-for-ui (:name email-field))])
;         (val/check-spec email-field "" t))))
;
;(deftest notstring
;  (is (= (t [:crudify/not-string] [(u/prepare-str-for-ui (:name email-field))])
;         (val/check-spec email-field nil t))))
;
;(deftest notnumber
;  (is (= (t [:crudify/not-number] [(u/prepare-str-for-ui (:name number-field))])
;         (val/check-spec number-field "sdlfkj" t))))
;
;(deftest max-length
;    (is (= (t [:crudify/max-length] [(u/prepare-str-for-ui (:name email-field))])
;           (val/check-spec email-field "sldkfasdfawerwerwerjsldkfasdfawerwerwerjsldkfasdfawerwerwerjsldkfasdfawerwerwerjsldkfasdfawerwerwerj" t))))

(deftest date
  (println (val/check-spec date-field "2017-12-28" t)))
  ;(is (= "" (val/check-spec date-field "2017-12-28" t))))

;(deftest notdate
;  (is (= (t [:crudify/not-number] [(u/prepare-str-for-ui (:name number-field))])
;         (val/check-spec number-field "sdlfkj" t))))
