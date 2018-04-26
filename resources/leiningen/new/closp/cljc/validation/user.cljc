(ns {{ns}}.cljc.validation.user
  (:require [phrase.alpha :refer [defphraser]]
            [clojure.spec.alpha :as s]
            [{{ns}}.cljc.locale :as loc]))



(s/def ::password #(<= 5 (count %)))

(s/def ::email #(not (nil? (re-matches #".+@.+\..+" %))))



(defphraser #(<= min_length (count %)) [_ _ min_length] (loc/localize [:generic/should-contain-x-chars] [min_length]))

(defphraser #(not (nil? (re-matches #".+@.+\..+" %))) [_ _] (loc/localize [:user/email_invalid]))



