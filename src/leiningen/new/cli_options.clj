(ns leiningen.new.cli-options
  (:require
    [clojure.string :as string]))

(def cli-options
  [["-n" "--namespace NAMESPACE" "Namespace for the new project"
    :default "closp"
    :parse-fn #(str %)]
   ["-h" "--help"]])

(defn usage [options-summary]
  (->> ["This is closp. A leiningen template to generate a base for web development in clj / cljs"
        ""
        "Usage: lein new closp [options]"
        ""
        "Options:"
        options-summary
        ""
        "Please refer to the manual page for more information."]
       (string/join \newline)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors)))

(defn exit [status msg]
  (println msg)
  (System/exit status))
