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
               [(str "src/{{san-path}}/session.clj") (*render* "gitignore")]
               ;["project.clj"                                              (*render* "project.clj")]
               ;["Procfile"                                                 (*render* "Procfile")]
               ;["README.md"                                                (*render* "README.md")]
               ;;; core namespaces
               ;["src/{{sanitized}}/session.clj"                            (*render* "session.clj")]
               ;["src/{{sanitized}}/handler.clj"                            (*render* "handler.clj")]
               ;["src/{{sanitized}}/middleware.clj"                         (*render* "middleware.clj")]
               ;["src/{{sanitized}}/repl.clj"                               (*render* "repl.clj")]
               ;["src/{{sanitized}}/util.clj"                               (*render* "util.clj")]
               ;["src/{{sanitized}}/routes/home.clj"                        (*render* "home.clj")]
               ;["src/{{sanitized}}/layout.clj"                             (*render* "layout.clj")]
               ;;; public resources, example URL: /css/screen.css
               ;
               ;["resources/public/css/screen.css"                          (*render* "screen.css")]
               ;["resources/public/md/docs.md"                              (*render* "docs.md")]
               ;"resources/public/js"
               ;"resources/public/img"
               ;;; tests
               ;["test/{{sanitized}}/test/handler.clj" (*render* "handler_test.clj")]
               ]
              )))
    )

(defn closp
  "Create a new Luminus project"
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
