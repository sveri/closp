(ns leiningen.new.closp
  (:require [leiningen.new.templates :refer [renderer sanitize year name-to-path ->files]]
            [leiningen.core.main :as main]
            [leinjacker.utils :refer [lein-generation]]
            [leiningen.new.cli-options :as opt-helper]
            [clojure.tools.cli :as t-cli]
            [clojure.string :as string]
            [clojure.java.io :as io])
  (:import (java.io File)
           (java.util Properties)))

(declare ^{:dynamic true} *name*)
(declare ^{:dynamic true} *render*)
(def features (atom nil))

(def render (renderer "closp"))

(def proj-dir (io/file (System/getProperty "leiningen.original.pwd")))

(defn unpack
  [name-proj name-in name-out]
  (try
    (let [p (string/join "/" ["leiningen" "new" "closp" name-in])
          i (io/resource p)
          o (io/file proj-dir name-proj name-out)
          _ (io/make-parents o)
          is (io/input-stream i)
          os (io/output-stream o)]
      (io/copy is os)
      (.flush os))
    (catch Exception e (println "tried unpacking in: " name-in " to out: " name-out " with error: " e))))

(defn create-db-dir [proj-name]
  (io/make-parents (io/file proj-dir proj-name "db" ".keep")))



(defn read-project-version [groupid artifact]
  (-> (doto (Properties.)
        (.load (-> "META-INF/maven/%s/%s/pom.properties"
                   (format groupid artifact)
                   (io/resource)
                   (io/reader))))
      (.get "version")))

(defn app-version
  "app version"
  []
  (read-project-version "descjop" "lein-template"))

(defn generate-project [name feature-params data]
  (binding [*name* name
            *render* #((renderer "closp") % data)]
    (reset! features (-> feature-params))

    (println "Generating new CLOSP project named: " (str name " with version: " (app-version) " ..."))

    (apply (partial ->files data)
           (concat
             [[".gitignore" (*render* "gitignore")]
              ["project.clj" (*render* "project.clj")]
              [(str "src/clj/{{san-path}}/core.clj") (*render* "clj/core.clj")]
              [(str "src/clj/{{san-path}}/locale.clj") (*render* "clj/locale.clj")]
              [(str "src/clj/{{san-path}}/middleware.clj") (*render* "clj/middleware.clj")]
              [(str "src/clj/{{san-path}}/session.clj") (*render* "clj/session.clj")]
              [(str "src/clj/{{san-path}}/user.clj") (*render* "clj/user.clj")]


              [(str "src/clj/{{san-path}}/components/components.clj") (*render* "clj/components/components.clj")]
              [(str "src/clj/{{san-path}}/components/config.clj") (*render* "clj/components/config.clj")]
              [(str "src/clj/{{san-path}}/components/db.clj") (*render* "clj/components/db.clj")]
              [(str "src/clj/{{san-path}}/components/handler.clj") (*render* "clj/components/handler.clj")]
              [(str "src/clj/{{san-path}}/components/server.clj") (*render* "clj/components/server.clj")]

              [(str "src/clj/{{san-path}}/db/user.clj") (*render* "clj/db/user.clj")]
              [(str "src/clj/{{san-path}}/db/entities.clj") (*render* "clj/db/entities.clj")]

              [(str "src/clj/{{san-path}}/crudify/create.clj") (*render* "clj/crudify/create.clj")]
              [(str "src/clj/{{san-path}}/crudify/crudify.clj") (*render* "clj/crudify/crudify.clj")]
              [(str "src/clj/{{san-path}}/crudify/delete.clj") (*render* "clj/crudify/delete.clj")]
              [(str "src/clj/{{san-path}}/crudify/edit.clj") (*render* "clj/crudify/edit.clj")]
              [(str "src/clj/{{san-path}}/crudify/forms.clj") (*render* "clj/crudify/forms.clj")]
              [(str "src/clj/{{san-path}}/crudify/index.clj") (*render* "clj/crudify/index.clj")]
              [(str "src/clj/{{san-path}}/crudify/util.clj") (*render* "clj/crudify/util.clj")]
              [(str "src/clj/{{san-path}}/crudify/validation.clj") (*render* "clj/crudify/validation.clj")]


              [(str "src/clj/{{san-path}}/routes/home.clj") (*render* "clj/routes/home.clj")]
              [(str "src/clj/{{san-path}}/routes/crud.clj") (*render* "clj/routes/crud.clj")]
              [(str "src/clj/{{san-path}}/routes/user.clj") (*render* "clj/routes/user.clj")]

              [(str "src/clj/{{san-path}}/views/base.clj") (*render* "clj/views/base.clj")]
              [(str "src/clj/{{san-path}}/views/home.clj") (*render* "clj/views/home.clj")]
              [(str "src/clj/{{san-path}}/views/user.clj") (*render* "clj/views/user.clj")]

              [(str "src/clj/{{san-path}}/service/auth.clj") (*render* "clj/service/auth.clj")]
              [(str "src/clj/{{san-path}}/service/spec_utils.clj") (*render* "clj/service/spec_utils.clj")]
              [(str "src/clj/{{san-path}}/service/user.clj") (*render* "clj/service/user.clj")]

              [(str "src/cljs/{{san-path}}/core.cljs") (*render* "cljs/core.cljs")]
              [(str "src/cljs/{{san-path}}/helper.cljs") (*render* "cljs/helper.cljs")]
              [(str "src/cljs/{{san-path}}/ajax.cljs") (*render* "cljs/ajax.cljs")]


              [(str "env/dev/cljs/{{sanitized}}/dev.cljs") (*render* "env/dev/cljs/dev.cljs")]

              [(str "src/cljc/{{san-path}}/cljccore.cljc") (*render* "cljc/cljccore.cljc")]


              ["README.md" (*render* "README.md")]

              [(str "test/clj/{{san-path}}/db/user_test.clj") (*render* "test/clj/db/user_test.clj")]
              [(str "test/clj/{{san-path}}/crudify/validation_test.clj") (*render* "test/clj/crudify/validation_test.clj")]

              [(str "integtest/clj/{{san-path}}/setup.clj") (*render* "integtest/clj/setup.clj")]
              [(str "integtest/clj/{{san-path}}/web/signup.clj") (*render* "integtest/clj/web/signup.clj")]
              [(str "integtest/clj/{{san-path}}/web/user.clj") (*render* "integtest/clj/web/user.clj")]
              [(str "integtest/clj/{{san-path}}/web/admin.clj") (*render* "integtest/clj/web/admin.clj")]

              ["resources/closp.edn" (*render* "resources/closp.edn")]
              ["resources/logback.xml" (*render* "resources/logback.xml")]

              ["migrators/h2/1-user.down.sql" (*render* "migrators/h2/1-user.down.sql")]
              ["migrators/h2/1-user.up.sql" (*render* "migrators/h2/1-user.up.sql")]
              ["migrators/sqlite/1-user.down.sql" (*render* "migrators/sqlite/1-user.down.sql")]
              ["migrators/sqlite/1-user.up.sql" (*render* "migrators/sqlite/1-user.up.sql")]
              ["migrators/postgres/1-user.up.sql" (*render* "migrators/postgres/1-user.up.sql")]
              ["migrators/postgres/1-user.down.sql" (*render* "migrators/postgres/1-user.down.sql")]]))



    (mapv #(apply unpack (:name data) %)
          [["resources/public/img/loading.gif" "resources/public/img/loading.gif"]

           ["resources/public/css/bootstrap.min.css" "resources/public/css/bootstrap.min.css"]
           ["resources/public/css/screen.css" "resources/public/css/screen.css"]
           ["resources/public/css/home.css" "resources/public/css/home.css"]
           ["resources/public/css/admin.css" "resources/public/css/admin.css"]

           ["resources/public/js/bootstrap.min.js" "resources/public/js/bootstrap.min.js"]
           ["resources/public/js/jquery-3.2.1.slim.min.js" "resources/public/js/jquery-3.2.1.slim.min.js"]
           ["resources/public/js/react-0.12.1.min.js" "resources/public/js/react-0.12.1.min.js"]

           ["resources/i18n/en.edn" "resources/i18n/en.edn"]
           ["resources/i18n/de.edn" "resources/i18n/de.edn"]

           ["env/dev/entities/user.edn" "env/dev/entities/user.edn"]])

    (create-db-dir (:name data))))

(defn closp
  "Create a new CLOSP project"
  [name & args]
  (let [{:keys [options _ errors summary]} (t-cli/parse-opts args opt-helper/cli-options)
        ns (:namespace options)
        san-path (string/replace ns #"\." "/")
        data {:name             name
              :sanitized        (sanitize name)
              :san-path         san-path
              :ns               ns
              :year             (year)
              :activationlink   "{{activationlink}}"
              :flash-alert-type "{{flash-alert-type}}"
              :flash-message    "{{flash-message}}"}]
    ;; Handle help and error conditions
    (cond
      (< (lein-generation) 2)
      (println "Leiningen version 2.x is required.")

      (re-matches #"\A\+.+" name)
      (println "Project name is missing.\nTry: lein new closp PROJECT_NAME" name (clojure.string/join " " args))

      (.exists (new File name))
      (println "Could not create project because a directory named" name "already exists!")

      (:help options) (opt-helper/exit 0 (opt-helper/usage summary))

      errors (opt-helper/exit 1 (opt-helper/error-msg errors))

      :else (generate-project name args data))))
