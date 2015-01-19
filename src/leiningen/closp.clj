(ns leiningen.closp
  (:require [leiningen.new.dependency-injector]
            [leiningen.new.template-parser]
            [leiningen.new.templates :refer [renderer sanitize year ->files]]
            [leinjacker.utils :refer [lein-generation]])
  ;(:use leiningen.new.dependency-injector
  ;      leiningen.new.template-parser
  ;      [leiningen.new.templates :only [renderer sanitize year ->files]]
  ;      [leinjacker.utils :only [lein-generation]])
  (:import java.io.File
           java.util.regex.Matcher))

(declare ^{:dynamic true} *name*)
(declare ^{:dynamic true} *render*)
(def features (atom nil))

(defn format-features [features]
  (apply str (interpose ", " features)))

(defn generate-project [name feature-params data]
  (binding [*name*   name
            *render* #((renderer "luminus") % data)]
    (reset! features (-> feature-params))
    ;(reset! features (-> feature-params dailycred-params site-params db-required-features))

    (println "Generating a lovely new Luminus project named" (str name "..."))

    (apply (partial ->files data)
           (concat
             [[".gitignore"                                               (*render* "gitignore")]
              ["project.clj"                                              (*render* "project.clj")]
              ["Procfile"                                                 (*render* "Procfile")]
              ["README.md"                                                (*render* "README.md")]
              ;; core namespaces
              ["src/{{sanitized}}/session.clj"                            (*render* "session.clj")]
              ["src/{{sanitized}}/handler.clj"                            (*render* "handler.clj")]
              ["src/{{sanitized}}/middleware.clj"                         (*render* "middleware.clj")]
              ["src/{{sanitized}}/repl.clj"                               (*render* "repl.clj")]
              ["src/{{sanitized}}/util.clj"                               (*render* "util.clj")]
              ["src/{{sanitized}}/routes/home.clj"                        (*render* "home.clj")]
              ["src/{{sanitized}}/layout.clj"                             (*render* "layout.clj")]
              ;; public resources, example URL: /css/screen.css

              ["resources/public/css/screen.css"                          (*render* "screen.css")]
              ["resources/public/md/docs.md"                              (*render* "docs.md")]
              "resources/public/js"
              "resources/public/img"
              ;; tests
              ["test/{{sanitized}}/test/handler.clj" (*render* "handler_test.clj")]]
             (when-not (some #{"+cljs"} @features)
               [["resources/templates/base.html"                            (*render* "templates/base.html")]
                ["resources/templates/home.html"                            (*render* "templates/home.html")]
                ["resources/templates/about.html"                           (*render* "templates/about.html")]])
             ;(include-features)
             ))
    ;(inject-dependencies)
    ))

(defn closp
  "I don't do a lot."
  [project & args]
  (let [supported-features #{"+cljs" "+site" "+h2" "+postgres" "+dailycred" "+mysql" "+http-kit" "+cucumber" "+mongodb"}
        data {:name name
              :sanitized (sanitize name)
              :year (year)}
        unsupported (-> (set args)
                        (clojure.set/difference supported-features)
                        (not-empty))
        feature-params (set args)]

    (cond
      (< (lein-generation) 2)
      (println "Leiningen version 2.x is required.")

      (re-matches #"\A\+.+" name)
      (println "Project name is missing.\nTry: lein new luminus PROJECT_NAME"
               name (clojure.string/join " " feature-params))

      unsupported
      (println "Unrecognized options:" (format-features unsupported)
               "\nSupported options are:" (format-features supported-features))

      (.exists (new File name))
      (println "Could not create project because a directory named" name "already exists!")

      :else
      (generate-project name feature-params data))))
