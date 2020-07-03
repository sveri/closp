(ns clj.new.closp
  (:require [clj.new.templates :refer [renderer project-data ->files] :as cljnew]))

;(project-data name) =>
;{:date "2020-01-22",
; :group "de.sveri",
; :name "soc",
; :sanitized "soc",
; :year 2020,
; :template-nested-dirs "{{nested-dirs}}",
; :artifact "soc",
; :developer "Sveri",
; :nested-dirs "de\\sveri\\soc",
; :version "0.1.0-SNAPSHOT",
; :namespace "de.sveri.soc",
; :user "sveri",
; :raw-name "de/sveri/soc"}

(defn closp
  "FIXME: write documentation"
  [args]
  (let [render (renderer "closp")
        data (project-data args)
        project-name (:name data)]

    (println "Generating fresh 'clj new' closp project with name: " project-name)
    (->files data
             ["deps.edn" (render "deps.edn" data)]
             [".gitignore" (render "gitignore" data)]
             ["package.json" (render "package.json" data)]
             ["README.md" (render "README.md" data)]
             ["shadow-cljs.edn" (render "shadow-cljs.edn" data)]
             ["cypress.json" (render "cypress.json" data)]

             ;migrators
             ["migrators/postgres/1-user.down.sql" (render "migrators/postgres/1-user.down.sql" data)]
             ["migrators/postgres/1-user.up.sql" (render "migrators/postgres/1-user.up.sql" data)]

             ;resources
             ["resources/closp.edn" (render "resources/closp.edn" data)]
             ["resources/log4j.properties" (render "resources/log4j.properties" data)]

             ["resources/i18n/de.edn" (render "resources/i18n/de.edn" data)]
             ["resources/i18n/en.edn" (render "resources/i18n/en.edn" data)]

             ["resources/public/css/material-icons.css" (render "resources/public/css/material-icons.css" data)]
             ["resources/public/css/materialize.min.css" (render "resources/public/css/materialize.min.css" data)]
             ["resources/public/css/screen.css" (render "resources/public/css/screen.css" data)]

             ["resources/public/js/material.init.js" (render "resources/public/js/material.init.js" data)]
             ["resources/public/js/materialize.min.js" (render "resources/public/js/materialize.min.js" data)]

             ;clj sources
             ["src/clj/user.clj" (render "src/clj/user.clj" data)]

             ["src/clj/{{nested-dirs}}/core.clj" (render "src/clj/core.clj" data)]
             ["src/clj/{{nested-dirs}}/locale.clj" (render "src/clj/locale.clj" data)]

             ["src/clj/{{nested-dirs}}/components/components.clj" (render "src/clj/components/components.clj" data)]
             ["src/clj/{{nested-dirs}}/components/config.clj" (render "src/clj/components/config.clj" data)]
             ["src/clj/{{nested-dirs}}/components/db.clj" (render "src/clj/components/db.clj" data)]
             ["src/clj/{{nested-dirs}}/components/handler.clj" (render "src/clj/components/handler.clj" data)]
             ["src/clj/{{nested-dirs}}/components/server.clj" (render "src/clj/components/server.clj" data)]

             ["src/clj/{{nested-dirs}}/db/user.clj" (render "src/clj/db/user.clj" data)]

             ["src/clj/{{nested-dirs}}/routes/home.clj" (render "src/clj/routes/home.clj" data)]
             ["src/clj/{{nested-dirs}}/routes/user.clj" (render "src/clj/routes/user.clj" data)]

             ["src/clj/{{nested-dirs}}/service/auth.clj" (render "src/clj/service/auth.clj" data)]
             ["src/clj/{{nested-dirs}}/service/user_service.clj" (render "src/clj/service/user_service.clj" data)]

             ["src/clj/{{nested-dirs}}/views/home.clj" (render "src/clj/views/home.clj" data)]
             ["src/clj/{{nested-dirs}}/views/user.clj" (render "src/clj/views/user.clj" data)]
             ["src/clj/{{nested-dirs}}/views/util.clj" (render "src/clj/views/util.clj" data)]

             ;cljs sources
             ["src/cljs/{{nested-dirs}}/config.cljs" (render "src/cljs/config.cljs" data)]
             ["src/cljs/{{nested-dirs}}/core.cljs" (render "src/cljs/core.cljs" data)]
             ["src/cljs/{{nested-dirs}}/db.cljs" (render "src/cljs/db.cljs" data)]
             ["src/cljs/{{nested-dirs}}/events.cljs" (render "src/cljs/events.cljs" data)]
             ["src/cljs/{{nested-dirs}}/routes.cljs" (render "src/cljs/routes.cljs" data)]
             ["src/cljs/{{nested-dirs}}/subs.cljs" (render "src/cljs/subs.cljs" data)]
             ["src/cljs/{{nested-dirs}}/views.cljs" (render "src/cljs/views.cljs" data)]

             ; test sources
             ["test/clj/{{nested-dirs}}/rest_setup.clj" (render "test/clj/rest_setup.clj" data)]

             ["cypress/integration/default_admin_workflow.js" (render "cypress/integration/default_admin_workflow.js" data)]
             ["cypress/integration/default_user_registration_login_workflow.js" (render "cypress/integration/default_user_registration_login_workflow.js" data)]

             ["cypress/support/index.js" (render "cypress/support/index.js" data)]
             ["cypress/support/user_support.js" (render "cypress/support/user_support.js" data)]

             ["test/resources/pg_reset.js" (render "test/resources/pg_reset.js" data)])))




