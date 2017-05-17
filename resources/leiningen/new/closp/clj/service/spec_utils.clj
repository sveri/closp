(ns {{ns}}.service.spec-utils
  (:require [clojure.spec :as s]
            [clojure.string :as str]))


(s/def ::non-empty-string (s/and string? #(not (str/blank? %))))
