(ns leiningen.new.closp
  (:require [leiningen.new.templates :refer [renderer sanitize year name-to-path ->files]]
            [leiningen.core.main :as main]
            [leinjacker.utils :refer [lein-generation]]
            [leiningen.new.cli-options :as opt-helper]
            [clojure.tools.cli :as t-cli]
            [clojure.string :as string])
  (:import (java.io File)))

(declare ^{:dynamic true} *name*)
(declare ^{:dynamic true} *render*)
(def features (atom nil))

(def render (renderer "closp"))


(defn generate-project [name feature-params data]
  (binding [*name* name
            *render* #((renderer "closp") % data)]
    (reset! features (-> feature-params))
    ;(reset! features (-> feature-params dailycred-params site-params db-required-features))

    (println "Generating new CLOSP project named" (str name "..."))

      (apply (partial ->files data)
            (concat
              [[".gitignore" (*render* "gitignore")]
               ["project.clj" (*render* "project.clj")]
               [(str "src/clj/{{san-path}}/dev.clj") (*render* "clj/dev.clj")]
               [(str "src/clj/{{san-path}}/server.clj") (*render* "clj/server.clj")]
               [(str "src/cljs/{{san-path}}/core.cljs") (*render* "cljs/core.cljs")]
               [(str "src/cljx/{{san-path}}/core.cljx") (*render* "cljx/core.cljx")]
               [(str "resources/templates/index.html") (*render* "resources/templates/index.html")]
               [(str "env/dev/cljs/{{sanitized}}/dev.cljs") (*render* "env/dev/cljs/dev.cljs")]
               [(str "env/prod/cljs/{{sanitized}}/prod.cljs") (*render* "env/prod/cljs/prod.cljs")]
               ]
              )))
    )

(defn closp
  "Create a new CLOSP project"
  [name & args]
  (let [{:keys [options arguments errors summary]} (t-cli/parse-opts args opt-helper/cli-options)
        ns (:namespace options)
        san-path (string/replace ns #"\." "/")
        data {:name      name
              :sanitized (sanitize name)
              :san-path    san-path
              :ns     ns
              :year      (year)}]
    ;; Handle help and error conditions
    (cond
      (< (lein-generation) 2)
      (println "Leiningen version 2.x is required.")

      (re-matches #"\A\+.+" name)
      (println "Project name is missing.\nTry: lein new closp PROJECT_NAME" name (clojure.string/join " " args))

      (.exists (new File name))
      (println "Could not create project because a directory named" name "already exists!")

      (:help options) (opt-helper/exit 0 (opt-helper/usage summary))
      ;(not= (count arguments) 1) (exit 1 (usage summary))
      
      errors (opt-helper/exit 1 (opt-helper/error-msg errors))
      
      :else (generate-project name args data))
    )
  )
