(ns leiningen.new.closp
  (:require [leiningen.new.templates :refer [renderer sanitize year name-to-path ->files]]
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


(defn generate-project [name feature-params data]
  (binding [*name* name
            *render* #((renderer "closp") % data)]
    (reset! features (-> feature-params))

    (println "Generating new CLOSP project named: " (str name " with version: 0.3.4-RC6 ..."))

    (apply (partial ->files data)
           (concat
             [[".gitignore" (*render* "gitignore")]
              ["project.clj" (*render* "project.clj")]
              [(str "src/clj/{{san-path}}/core.clj") (*render* "clj/core.clj")]
              [(str "src/clj/{{san-path}}/locale.clj") (*render* "clj/locale.clj")]
              [(str "src/clj/{{san-path}}/middleware.clj") (*render* "clj/middleware.clj")]
              [(str "src/clj/{{san-path}}/user.clj") (*render* "clj/user.clj")]


              [(str "src/clj/{{san-path}}/components/components.clj") (*render* "clj/components/components.clj")]
              [(str "src/clj/{{san-path}}/components/config.clj") (*render* "clj/components/config.clj")]
              [(str "src/clj/{{san-path}}/components/db.clj") (*render* "clj/components/db.clj")]
              [(str "src/clj/{{san-path}}/components/handler.clj") (*render* "clj/components/handler.clj")]
              [(str "src/clj/{{san-path}}/components/server.clj") (*render* "clj/components/server.clj")]

              [(str "src/clj/{{san-path}}/db/user.clj") (*render* "clj/db/user.clj")]

              [(str "src/clj/{{san-path}}/routes/api.clj") (*render* "clj/routes/api.clj")]
              [(str "src/clj/{{san-path}}/routes/home.clj") (*render* "clj/routes/home.clj")]
              [(str "src/clj/{{san-path}}/routes/user.clj") (*render* "clj/routes/user.clj")]
              [(str "src/clj/{{san-path}}/routes/util.clj") (*render* "clj/routes/util.clj")]

              [(str "src/clj/{{san-path}}/views/base.clj") (*render* "clj/views/base.clj")]

              [(str "src/clj/{{san-path}}/service/auth.clj") (*render* "clj/service/auth.clj")]
              [(str "src/clj/{{san-path}}/service/config.clj") (*render* "clj/service/config.clj")]
              [(str "src/clj/{{san-path}}/service/user.clj") (*render* "clj/service/user.clj")]

              [(str "src/cljs/{{san-path}}/common.cljs") (*render* "cljs/common.cljs")]
              [(str "src/cljs/{{san-path}}/config.cljs") (*render* "cljs/config.cljs")]
              [(str "src/cljs/{{san-path}}/core.cljs") (*render* "cljs/core.cljs")]
              [(str "src/cljs/{{san-path}}/db.cljs") (*render* "cljs/db.cljs")]
              [(str "src/cljs/{{san-path}}/effects.cljs") (*render* "cljs/effects.cljs")]
              [(str "src/cljs/{{san-path}}/events.cljs") (*render* "cljs/events.cljs")]
              [(str "src/cljs/{{san-path}}/helper.cljs") (*render* "cljs/helper.cljs")]
              [(str "src/cljs/{{san-path}}/localstorage.cljs") (*render* "cljs/localstorage.cljs")]
              [(str "src/cljs/{{san-path}}/routes.cljs") (*render* "cljs/routes.cljs")]
              [(str "src/cljs/{{san-path}}/subs.cljs") (*render* "cljs/subs.cljs")]
              [(str "src/cljs/{{san-path}}/views.cljs") (*render* "cljs/views.cljs")]

              [(str "src/cljs/{{san-path}}/home/views.cljs") (*render* "cljs/home/views.cljs")]

              [(str "src/cljs/{{san-path}}/home/third_party/http_fx.cljs") (*render* "cljs/third_party/http_fx.cljs")]

              [(str "src/cljs/{{san-path}}/user/events.cljs") (*render* "cljs/user/events.cljs")]
              [(str "src/cljs/{{san-path}}/user/helper.cljs") (*render* "cljs/user/helper.cljs")]
              [(str "src/cljs/{{san-path}}/user/subs.cljs") (*render* "cljs/user/subs.cljs")]
              [(str "src/cljs/{{san-path}}/user/views.cljs") (*render* "cljs/user/views.cljs")]


              [(str "src/cljc/{{san-path}}/cljc/locale.cljc") (*render* "cljc/locale.cljc")]
              [(str "src/cljc/{{san-path}}/cljc/validation/user.cljc") (*render* "cljc/validation/user.cljc")]


              ["README.md" (*render* "README.md")]

              [(str "test/clj/{{san-path}}/db/user_test.clj") (*render* "test/clj/db/user_test.clj")]

              [(str "integtest/clj/{{san-path}}/setup.clj") (*render* "integtest/clj/setup.clj")]
              [(str "integtest/clj/{{san-path}}/web/signup.clj") (*render* "integtest/clj/web/signup.clj")]
              [(str "integtest/clj/{{san-path}}/web/user.clj") (*render* "integtest/clj/web/user.clj")]
              [(str "integtest/clj/{{san-path}}/web/admin.clj") (*render* "integtest/clj/web/admin.clj")]

              ["resources/closp.edn" (*render* "resources/closp.edn")]
              ["resources/logback.xml" (*render* "resources/logback.xml")]

              ["migrators/postgres/1-user.up.sql" (*render* "migrators/postgres/1-user.up.sql")]
              ["migrators/postgres/1-user.down.sql" (*render* "migrators/postgres/1-user.down.sql")]]))



    (mapv #(apply unpack (:name data) %)
          [["resources/public/img/loading.gif" "resources/public/img/loading.gif"]

           ["resources/public/css/bootstrap.min.css" "resources/public/css/bootstrap.min.css"]

           ["resources/public/js/bootstrap.min.js" "resources/public/js/bootstrap.min.js"]
           ["resources/public/js/jquery-3.3.1.min.js" "resources/public/js/jquery-3.3.1.min.js"]

           ["resources/i18n/en.edn" "resources/i18n/en.edn"]
           ["resources/i18n/de.edn" "resources/i18n/de.edn"]])

    (create-db-dir (:name data))))

(defn closp
  "Create a new CLOSP project"
  [name & args]
  (let [{:keys [options _ errors summary]} (t-cli/parse-opts args opt-helper/cli-options)
        ns (:namespace options)
        san-path (string/replace ns #"\." "/")
        data {:name      name
              :sanitized (sanitize name)
              :san-path  san-path
              :ns        ns
              :year      (year)
              :jwt-key   (apply str (take 40 (repeatedly #(char (+ (rand 26) 65)))))}]
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
