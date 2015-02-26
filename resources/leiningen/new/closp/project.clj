(defproject {{name}} "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :source-paths ["src/clj" "src/cljs" "target/generated/clj" "target/generated/cljs"]

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2740" :scope "provided"]

                 [org.clojure/core.cache "0.6.4"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]

                 [ring "1.3.2"]
                 [lib-noir "0.9.5"]
                 [ring-server "0.4.0"]
                 [ring/ring-anti-forgery "1.0.0"]
                 [compojure "1.3.1"]
                 [reagent "0.4.3"]
                 [figwheel "0.1.4-SNAPSHOT"]
                 [environ "1.0.0"]
                 [com.cemerick/piggieback "0.1.5"]
                 [weasel "0.5.0"]
                 [leiningen "2.5.1"]
                 [http-kit "2.1.19"]
                 [selmer "0.8.0"]
                 [prone "0.8.0"]
                 [im.chit/cronj "1.4.3"]
                 [com.taoensso/timbre "3.3.1"]
                 [noir-exception "0.2.3"]

                 [buddy/buddy-auth "0.3.0"]
                 [buddy/buddy-hashers "0.3.0"]

                 [log4j "1.2.17" :exclusions [javax.mail/mail
                                              javax.jms/jms
                                              com.sun.jdmk/jmxtools
                                              com.sun.jmx/jmxri]]

                 [org.clojure/java.jdbc "0.3.6"]
                 [korma "0.4.0"]
                 [com.h2database/h2 "1.4.185"]
                 [joplin.core "0.2.9"]

                 [com.draines/postal "1.11.3"]

                 [jarohen/nomad "0.7.0"]

                 [de.sveri/clojure-commons "0.1.9"]

                 [clojure-miniprofiler "0.2.8"]

                 [org.danielsz/system "0.1.4"]

                 [datascript "0.9.0"]
                 [org.clojars.franks42/cljs-uuid-utils "0.1.3"]

                 [net.tanesha.recaptcha4j/recaptcha4j "0.0.8"]]

  :plugins [[com.keminglabs/cljx "0.5.0"]
            [lein-cljsbuild "1.0.3"]
            [ragtime/ragtime.lein "0.3.8"]]

  ;database migrations
  :joplin {:migrators {:sql-mig "joplin/migrators/sql"}}

  :min-lein-version "2.5.0"

  :uberjar-name "{{name}}.jar"

  :cljsbuild
  {:builds {:dev {:source-paths ["src/cljs" "target/generated/cljs" "env/dev/cljs"]
                  :compiler     {:main           "{{name}}.dev"
                                 :asset-path     "/js/out"
                                 :output-to      "resources/public/js/app.js"
                                 :output-dir     "resources/public/js/out"
                                 :source-map     "resources/public/js/out.js.map"
                                 :optimizations  :none
                                 :cache-analysis true
                                 :pretty-print   true}}
            :adv {:source-paths ["src/cljs" "target/generated/cljs"]
                  :compiler     {:main          "{{ns}}.core"
                                 :output-to     "resources/public/js/app.js"
                                 :output-dir    "resources/public/js/out-adv"
                                 :source-map    "resources/public/js/out.js.map"
                                 :optimizations :advanced
                                 :pretty-print  false}}}}


  :prep-tasks [["cljx" "once"] "javac" "compile"]           ;also not sure

  :cljx {:builds [{:source-paths ["src/cljx"]
                   :output-path  "target/generated/clj"
                   :rules        :clj}
                  {:source-paths ["src/cljx"]
                   :output-path  "target/generated/cljs"
                   :rules        :cljs}]}

  :profiles {:dev     {:repl-options {:init-ns          {{ns}}.user
                                      :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

                       :plugins      [[lein-ring "0.9.0"]
                                      [lein-figwheel "0.1.4-SNAPSHOT"]
                                      [joplin.lein "0.2.9"]]

                       :figwheel     {:http-server-root "public"
                                      :port             3449
                                      :css-dirs         ["resources/public/css"]}

                       :dependencies [[ring-mock "0.1.5"]
                                      [ring/ring-devel "1.3.2"]
                                      [pjstadig/humane-test-output "0.6.0"]]

                       :injections   [(require 'pjstadig.humane-test-output)
                                      (pjstadig.humane-test-output/activate!)]

                       :joplin {:databases {:sql-dev {:type :sql, :url "jdbc:h2:./db/korma.db"}}
                                :environments {:sql-dev-env [{:db :sql-dev, :migrator :sql-mig}]}}}

             :uberjar {:auto-clean false                    ; not sure about this one
                       :omit-source true
                       :aot         :all
                       :cljsbuild {:builds {:adv {:compiler {:optimizations :advanced
                                                             :pretty-print false}}}}}}

  :main {{ns}}.core

  :aliases {"rel-jar" ["do" "clean," "cljx" "once," "cljsbuild" "clean," "cljsbuild" "once" "adv," "uberjar"]})
