(ns leiningen.new.closp
  (:require [leiningen.new.templates :refer [renderer sanitize year name-to-path ->files]]
            [leiningen.core.main :as main]
            [leinjacker.utils :refer [lein-generation]]
            [leiningen.new.cli-options :as opt-helper]
            [clojure.tools.cli :as t-cli]
            [clojure.string :as string]
            [clojure.java.io :as io])
  (:import (java.io File)))

(declare ^{:dynamic true} *name*)
(declare ^{:dynamic true} *render*)
(def features (atom nil))

(def render (renderer "closp"))

(def proj-dir (io/file (System/getProperty "leiningen.original.pwd")))
(defn unpack
  [name-proj name-in name-out]
  (let [p (string/join "/" ["leiningen" "new" "closp" name-in])
        i (io/resource p)
        o (io/file proj-dir name-proj name-out)
        _ (io/make-parents o)
        is (io/input-stream i)
        os (io/output-stream o)]
    (io/copy is os)
    (.flush os)))

(defn generate-project [name feature-params data]
  (binding [*name* name
            *render* #((renderer "closp") % data)]
    (reset! features (-> feature-params))

    (println "Generating new CLOSP project named" (str name "..."))

    (apply (partial ->files data)
           (concat
             [[".gitignore" (*render* "gitignore")]
              ["project.clj" (*render* "project.clj")]
              [(str "src/clj/{{san-path}}/dev.clj") (*render* "clj/dev.clj")]
              [(str "src/clj/{{san-path}}/handler.clj") (*render* "clj/handler.clj")]
              [(str "src/clj/{{san-path}}/core.clj") (*render* "clj/core.clj")]
              [(str "src/clj/{{san-path}}/layout.clj") (*render* "clj/layout.clj")]
              [(str "src/clj/{{san-path}}/middleware.clj") (*render* "clj/middleware.clj")]
              [(str "src/clj/{{san-path}}/repl.clj") (*render* "clj/repl.clj")]
              [(str "src/clj/{{san-path}}/session.clj") (*render* "clj/session.clj")]
              [(str "src/clj/{{san-path}}/util.clj") (*render* "clj/util.clj")]

              [(str "src/clj/{{san-path}}/routes/home.clj") (*render* "clj/routes/home.clj")]
              [(str "src/clj/{{san-path}}/routes/user.clj") (*render* "clj/routes/user.clj")]

              [(str "src/clj/{{san-path}}/service/auth.clj") (*render* "clj/service/auth.clj")]
              [(str "src/clj/{{san-path}}/service/user.clj") (*render* "clj/service/user.clj")]

              [(str "src/cljs/{{san-path}}/core.cljs") (*render* "cljs/core.cljs")]
              [(str "src/cljs/{{san-path}}/helper.cljs") (*render* "cljs/helper.cljs")]
              [(str "env/dev/cljs/{{sanitized}}/dev.cljs") (*render* "env/dev/cljs/dev.cljs")]
              [(str "env/prod/cljs/{{sanitized}}/prod.cljs") (*render* "env/prod/cljs/prod.cljs")]

              [(str "src/cljx/{{san-path}}/clj-core.cljx") (*render* "cljx/clj-core.cljx")]


              [(str "resources/templates/base.html") (*render* "resources/templates/base.html")]
              [(str "resources/templates/menu.html") (*render* "resources/templates/menu.html")]
              [(str "resources/templates/profile.html") (*render* "resources/templates/profile.html")]
              [(str "resources/templates/registration.html") (*render* "resources/templates/registration.html")]
              [(str "resources/templates/home/agb.html") (*render* "resources/templates/home/agb.html")]
              [(str "resources/templates/home/contact.html") (*render* "resources/templates/home/contact.html")]
              [(str "resources/templates/home/cookies.html") (*render* "resources/templates/home/cookies.html")]
              [(str "resources/templates/home/index.html") (*render* "resources/templates/home/index.html")]
              [(str "resources/templates/home/tos.html") (*render* "resources/templates/home/tos.html")]
              [(str "resources/templates/user/admin.html") (*render* "resources/templates/user/admin.html")]
              [(str "resources/templates/user/login.html") (*render* "resources/templates/user/login.html")]
              [(str "resources/templates/user/signup.html") (*render* "resources/templates/user/signup.html")]

              [(str "resources/public/css/screen.css") (*render* "resources/public/css/screen.css")]
              [(str "resources/public/css/home.css") (*render* "resources/public/css/home.css")]

              [(str "migrations/2015-011-26-add-users-table.down.sql") (*render* "migrations/2015-011-26-add-users-table.down.sql")]
              [(str "migrations/2015-011-26-add-users-table.up.sql") (*render* "migrations/2015-011-26-add-users-table.up.sql")]
              
              "target/generated/cljs"
              "target/generated/clj"
              ]))


    (mapv #(apply unpack (:sanitized data) %)
          [["resources/public/img/loading.gif" "resources/public/img/loading.gif"]])))

(defn closp
  "Create a new CLOSP project"
  [name & args]
  (let [{:keys [options arguments errors summary]} (t-cli/parse-opts args opt-helper/cli-options)
        ns (:namespace options)
        san-path (string/replace ns #"\." "/")
        data {:name      name
              :sanitized (sanitize name)
              :san-path  san-path
              :ns        ns
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
