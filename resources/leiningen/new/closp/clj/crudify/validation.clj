(ns {{ns}}.crudify.validation
  (:require [clojure.string :as str]
            [{{ns}}.crudify.util :as u]
            [clojure.spec :as s]))



(defn check-spec [field value localize]
  (when-let [error-data (s/explain-data (:spec field) value)]
    (reduce (fn [error-string problem]
              (println problem)
              (let [first-via (-> problem :via first name)]
                (str error-string
                     (condp #(.startsWith %2 %1) first-via
                       "not-null" (localize [:crudify/not-null] [(u/prepare-str-for-ui (:name field))])
                       "not-empty" (localize [:crudify/not-empty] [(u/prepare-str-for-ui (:name field))])
                       "string?" (localize [:crudify/not-string] [(u/prepare-str-for-ui (:name field))])
                       "number?" (localize [:crudify/not-number] [(u/prepare-str-for-ui (:name field))])
                       "integer?" (localize [:crudify/not-integer] [(u/prepare-str-for-ui (:name field))])
                       "date?" (localize [:crudify/not-date] [(u/prepare-str-for-ui (:name field))])
                       "max" (localize [:crudify/max-length] [(u/prepare-str-for-ui (:name field))])
                       :else (str "Some error occured in " (:name field))))))
            ""
            (::s/problems error-data))))



(defn conform-field [field req]
  (let [form-value (get-in req [:form-params (:name field)])]
    ;(println (s/conform (:conformer field) form-value))
    (if-let [conformer (:conformer field)]
      (s/conform conformer form-value)
      form-value)))

(defn validate [entity localize req]
  (let [fields (:fields entity)
        err-string (reduce (fn [error-string field]
                             (if-let [error (check-spec field (conform-field field req) localize)]
                               (do (println error) (str error "<br>" error-string))
                               error-string))
                           ""
                           fields)]
    (when-not (str/blank? err-string) (throw (Exception. err-string)))))
