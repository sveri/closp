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

    (println "Generating new CLOSP project named" (str name "..."))

    (apply (partial ->files data)
           (concat
             [[".gitignore" (*render* "gitignore")]
              ["project.clj" (*render* "project.clj")]
              [(str "src/clj/{{san-path}}/core.clj") (*render* "clj/core.clj")]
              [(str "src/clj/{{san-path}}/layout.clj") (*render* "clj/layout.clj")]
              [(str "src/clj/{{san-path}}/middleware.clj") (*render* "clj/middleware.clj")]
              [(str "src/clj/{{san-path}}/session.clj") (*render* "clj/session.clj")]
              [(str "src/clj/{{san-path}}/user.clj") (*render* "clj/user.clj")]
              [(str "src/clj/{{san-path}}/types.clj") (*render* "clj/types.clj")]

              [(str "src/clj/{{san-path}}/components/components.clj") (*render* "clj/components/components.clj")]
              [(str "src/clj/{{san-path}}/components/config.clj") (*render* "clj/components/config.clj")]
              [(str "src/clj/{{san-path}}/components/db.clj") (*render* "clj/components/db.clj")]
              [(str "src/clj/{{san-path}}/components/handler.clj") (*render* "clj/components/handler.clj")]
              [(str "src/clj/{{san-path}}/components/server.clj") (*render* "clj/components/server.clj")]
              [(str "src/clj/{{san-path}}/components/locale.clj") (*render* "clj/components/locale.clj")]

              [(str "src/clj/{{san-path}}/db/user.clj") (*render* "clj/db/user.clj")]

              [(str "src/clj/{{san-path}}/routes/home.clj") (*render* "clj/routes/home.clj")]
              [(str "src/clj/{{san-path}}/routes/user.clj") (*render* "clj/routes/user.clj")]
              [(str "src/clj/{{san-path}}/routes/cc.clj") (*render* "clj/routes/cc.clj")]

              [(str "src/clj/{{san-path}}/service/auth.clj") (*render* "clj/service/auth.clj")]
              [(str "src/clj/{{san-path}}/service/user.clj") (*render* "clj/service/user.clj")]

              [(str "src/cljs/{{san-path}}/core.cljs") (*render* "cljs/core.cljs")]
              [(str "src/cljs/{{san-path}}/helper.cljs") (*render* "cljs/helper.cljs")]
              [(str "src/cljs/{{san-path}}/ajax.cljs") (*render* "cljs/ajax.cljs")]
              [(str "env/dev/cljs/{{sanitized}}/dev.cljs") (*render* "env/dev/cljs/dev.cljs")]

              [(str "src/cljc/{{san-path}}/cljccore.cljc") (*render* "cljc/cljccore.cljc")]


              [(str "resources/templates/base.html") (*render* "resources/templates/base.html")]
              [(str "resources/templates/home/example.html") (*render* "resources/templates/home/example.html")]

              ["README.md" (*render* "README.md")]

              [(str "test/clj/{{san-path}}/db/user_test.clj") (*render* "test/clj/db/user_test.clj")]

              [(str "integtest/clj/{{san-path}}/web/setup.clj") (*render* "integtest/clj/web/setup.clj")]
              [(str "integtest/clj/{{san-path}}/web/signup.clj") (*render* "integtest/clj/web/signup.clj")]
              [(str "integtest/clj/{{san-path}}/web/user.clj") (*render* "integtest/clj/web/user.clj")]
              [(str "integtest/clj/{{san-path}}/web/admin.clj") (*render* "integtest/clj/web/admin.clj")]

              ["resources/closp.edn" (*render* "resources/closp.edn")]
              ]))


    (mapv #(apply unpack (:name data) %)
          [["resources/public/img/loading.gif" "resources/public/img/loading.gif"]
           ["resources/templates/menu.html" "resources/templates/menu.html"]
           ["resources/templates/profile.html" "resources/templates/profile.html"]
           ["resources/templates/home/agb.html" "resources/templates/home/agb.html"]
           ["resources/templates/home/contact.html" "resources/templates/home/contact.html"]
           ["resources/templates/home/cookies.html" "resources/templates/home/cookies.html"]
           ["resources/templates/home/index.html" "resources/templates/home/index.html"]
           ["resources/templates/home/tos.html" "resources/templates/home/tos.html"]
           ["resources/templates/user/account-activated.html" "resources/templates/user/account-activated.html"]
           ["resources/templates/user/account-created.html" "resources/templates/user/account-created.html"]
           ["resources/templates/user/admin.html" "resources/templates/user/admin.html"]
           ["resources/templates/user/changepassword.html" "resources/templates/user/changepassword.html"]
           ["resources/templates/user/login.html" "resources/templates/user/login.html"]
           ["resources/templates/user/signup.html" "resources/templates/user/signup.html"]
           ["resources/templates/user/reallydelete.html" "resources/templates/user/reallydelete.html"]

           ["resources/public/css/bootstrap.min.css" "resources/public/css/bootstrap.min.css"]
           ["resources/public/css/bootstrap-theme.min.css" "resources/public/css/bootstrap-theme.min.css"]
           ["resources/public/css/screen.css" "resources/public/css/screen.css"]
           ["resources/public/css/home.css" "resources/public/css/home.css"]

           ["resources/public/js/bootstrap.min.js" "resources/public/js/bootstrap.min.js"]
           ["resources/public/js/jquery-2.0.3.min.js" "resources/public/js/jquery-2.0.3.min.js"]
           ["resources/public/js/react-0.12.1.min.js" "resources/public/js/react-0.12.1.min.js"]

           ["env/dev/user.edn" "env/dev/user.edn"]

           ["migrators/h2/user-20150720T132915Z.down.sql" "resources/migrators/h2/user-20150720T132915Z.down.sql"]
           ["migrators/h2/user-20150720T132915Z.up.sql" "resources/migrators/h2/user-20150720T132915Z.up.sql"]
           ["migrators/sqlite/user-20150720T083449Z.down.sql" "resources/migrators/sqlite/user-20150720T083449Z.down.sql"]
           ["migrators/sqlite/user-20150720T083449Z.up.sql" "resources/migrators/sqlite/user-20150720T083449Z.up.sql"]

           ["migrators/sqlite/user-20150720T083449Z.up.sql" "resources/migrators/sqlite/user-20150720T083449Z.up.sql"]])

    (create-db-dir (:name data))))

(defn closp
  "Create a new CLOSP project"
  [name & args]
  (let [{:keys [options arguments errors summary]} (t-cli/parse-opts args opt-helper/cli-options)
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

      :else (generate-project name args data))
    )
  )
