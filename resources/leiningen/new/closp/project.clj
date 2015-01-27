(defproject {{name}} "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :source-paths ["src/clj" "src/cljs" "target/generated/clj" "target/generated/cljx"]

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2371" :scope "provided"]

                 [org.clojure/core.cache "0.6.4"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 
                 [ring "1.3.1"]
                 [lib-noir "0.9.5"]
                 [ring-server "0.3.1"]
                 [compojure "1.2.0"]
                 [reagent "0.4.3"]
                 [figwheel "0.1.4-SNAPSHOT"]
                 [environ "1.0.0"]
                 [com.cemerick/piggieback "0.1.3"]
                 [weasel "0.4.0-SNAPSHOT"]
                 [leiningen "2.5.0"]
                 [http-kit "2.1.19"]
                 [selmer "0.7.9"]
                 [prone "0.8.0"]
                 [im.chit/cronj "1.4.3"]
                 [com.taoensso/timbre "3.3.1"]
                 [noir-exception "0.2.3"]

                 [buddy/buddy-auth "0.3.0-SNAPSHOT"]

                 [log4j "1.2.15" :exclusions [javax.mail/mail
                                              javax.jms/jms
                                              com.sun.jdmk/jmxtools
                                              com.sun.jmx/jmxri]]

                 [org.clojure/java.jdbc "0.3.6"]
                 [korma "0.4.0"]
                 [com.h2database/h2 "1.4.182"]
                 [ragtime/ragtime.sql.files "0.3.8"]]

  :plugins [[lein-cljsbuild "1.0.3"]
            [lein-environ "1.0.0"]
            [ragtime/ragtime.lein "0.3.8"]]

  ;database migrations
  :ragtime {:migrations ragtime.sql.files/migrations
            :database "jdbc:h2:~/closp2"}

  :min-lein-version "2.5.0"

  :uberjar-name "{{name}}.jar"

  :cljsbuild {:builds {:app {:source-paths ["src/cljs" "target/generated/cljs"]
                             :compiler {:output-to     "resources/public/js/app.js"
                                        :output-dir    "resources/public/js/out"
                                        :source-map    "resources/public/js/out.js.map"
                                        :preamble      ["react/react.min.js"]
                                        :externs       ["react/externs/react.js"]
                                        :optimizations :none
                                        :pretty-print  true}}}}

  

  :profiles {:dev {:prep-tasks [["cljx" "once"]]
                   :repl-options {:init-ns {{ns}}.repl
                                  :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

                   :plugins [[com.keminglabs/cljx "0.5.0"]
                             [lein-ring "0.9.0"]
                             [lein-environ "1.0.0"]
                             [lein-ancient "0.5.5"]
                             [lein-figwheel "0.1.4-SNAPSHOT"]]

                   :figwheel {:http-server-root "public"
                              :port 3449
                              :css-dirs ["resources/public/css"]}

                   :env {:dev true}

                   :dependencies [[ring-mock "0.1.5"]
                                  [ring/ring-devel "1.3.2"]
                                  [pjstadig/humane-test-output "0.6.0"]]
                   
                   :injections [(require 'pjstadig.humane-test-output)
                                (pjstadig.humane-test-output/activate!)]

                   :cljx {:builds [{:source-paths ["src/cljx"]
                                    :output-path "target/generated/clj"
                                    :rules :clj}
                                   {:source-paths ["src/cljx"]
                                    :output-path "target/generated/cljs"
                                    :rules :cljs}]}

                   :cljsbuild {:builds {:app {:source-paths ["env/dev/cljs"]}}}}

             :uberjar {:prep-tasks [["cljx" "once"]]
                       :env {:production true}
                       :omit-source true
                       :aot :all
                       :cljsbuild {:builds {:app
                                            {:source-paths ["env/prod/cljs"]
                                             :compiler
                                             {:optimizations :advanced
                                              :pretty-print false}}}}}})
