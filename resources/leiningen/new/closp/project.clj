(defproject {{name}} "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :source-paths ["src/clj" "src/cljs" "src/cljc"]

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "0.0-3308"]

                 [org.clojure/core.cache "0.6.4"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]

                 [ring "1.4.0"]
                 [lib-noir "0.9.9"]
                 [ring-server "0.4.0"]
                 [ring/ring-anti-forgery "1.0.0"]
                 [compojure "1.4.0"]
                 [reagent "0.5.0"]
                 [environ "1.0.0"]
                 [leiningen "2.5.1"]
                 [http-kit "2.1.19"]
                 [selmer "0.8.5"]
                 [prone "0.8.2"]
                 [im.chit/cronj "1.4.3"]
                 [com.taoensso/timbre "3.2.1"]
                 [noir-exception "0.2.5"]

                 [buddy/buddy-auth "0.6.0"]
                 [buddy/buddy-hashers "0.6.0"]

                 [log4j "1.2.17" :exclusions [javax.mail/mail
                                              javax.jms/jms
                                              com.sun.jdmk/jmxtools
                                              com.sun.jmx/jmxri]]

                 [org.clojure/java.jdbc "0.3.7"]
                 [korma "0.4.2"]
                 [com.h2database/h2 "1.4.187"]
                 [org.xerial/sqlite-jdbc "3.8.10.1"]

                 [com.draines/postal "1.11.3"]

                 [jarohen/nomad "0.7.1"]

                 [de.sveri/clojure-commons "0.2.0"]

                 [clojure-miniprofiler "0.4.0"]

                 [org.danielsz/system "0.1.8"]

                 [datascript "0.11.6"]
                 [org.clojars.franks42/cljs-uuid-utils "0.1.3"]

                 [net.tanesha.recaptcha4j/recaptcha4j "0.0.8"]

                 [org.clojure/core.typed "0.3.11"]]

  :plugins [[de.sveri/closp-crud "0.1.3"]
            [lein-cljsbuild "1.0.5"]]

  ;database migrations
  :joplin {:migrators {:sqlite-mig "resources/migrators/sqlite"
                       :h2-mig "resources/migrators/h2"}}

  :closp-crud {:jdbc-url "jdbc:sqlite:./db/{{name}}.sqlite"
               :migrations-output-path "./resources/migrators/sqlite"
               :clj-src "src/clj"
               :ns-db "{{ns}}.db"
               :ns-routes "{{ns}}.routes"
               :ns-layout "{{ns}}.layout"
               :templates "resources/templates"}

  :min-lein-version "2.5.0"

  ; leaving this commented because of: https://github.com/cursiveclojure/cursive/issues/369
  ;:hooks [leiningen.cljsbuild]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :cljsbuild
  {:builds {:dev {:source-paths ["src/cljs" "src/cljc" "env/dev/cljs"]
                  :figwheel {:css-dirs ["resources/public/css"]             ;; watch and update CSS
                             :on-jsload "{{name}}.dev/main  "}
                  :compiler     {:main           "{{name}}.dev"
                                 :asset-path     "/js/compiled/out"
                                 :output-to      "resources/public/js/compiled/app.js"
                                 :output-dir     "resources/public/js/compiled/out"
                                 :source-map     "resources/public/js/compiled/out.js.map"
                                 :optimizations  :none
                                 :cache-analysis true
                                 :pretty-print   true
                                 :source-map-timestamp true}}
            :adv {:source-paths ["src/cljs" "src/cljc"]
                  :compiler     {:output-to     "resources/public/js/compiled/app.js"
                                 ; leaving this commented because of: https://github.com/cursiveclojure/cursive/issues/369
                                 ;:jar           true
                                 :optimizations :advanced
                                 :pretty-print  false}}}}

  :profiles {:dev     {:repl-options {:init-ns          {{ns}}.user}

                       :plugins      [[lein-ring "0.9.0"]
                                      [lein-figwheel "0.3.3"]
                                      [joplin.lein "0.2.17"]]

                       :dependencies [[org.bouncycastle/bcprov-jdk15on "1.52"]

                                      ; use this for htmlunit or an older firefox version
                                      [clj-webdriver "0.6.1"
                                       :exclusions [org.seleniumhq.selenium/selenium-server]]

                                      ; uncomment this to use current firefox version (does not work with htmlunit
                                      ;[clj-webdriver "0.6.1"
                                      ; :exclusions
                                      ; [org.seleniumhq.selenium/selenium-server
                                      ;  org.seleniumhq.selenium/selenium-java
                                      ;  org.seleniumhq.selenium/selenium-remote-driver]]

                                      [org.seleniumhq.selenium/selenium-server "2.46.0"]
                                      [ring-mock "0.1.5"]
                                      [ring/ring-devel "1.4.0"]
                                      [pjstadig/humane-test-output "0.7.0"]
                                      [joplin.core "0.2.17"]
                                      [joplin.jdbc "0.2.17"]]

                       :injections   [(require 'pjstadig.humane-test-output)
                                      (pjstadig.humane-test-output/activate!)]

                       :joplin {:databases {:sqlite-dev {:type :sql, :url "jdbc:sqlite:./db/{{name}}.sqlite"}
                                            :h2-dev {:type :sql, :url "jdbc:h2:./db/korma.db;DATABASE_TO_UPPER=FALSE"}}
                                :environments {:sqlite-dev-env [{:db :sqlite-dev, :migrator :sqlite-mig}]
                                               :h2-dev-env [{:db :h2-dev, :migrator :h2-mig}]}}}

             :uberjar {:auto-clean false                    ; not sure about this one
                       :omit-source true
                       :aot         :all}}

  :test-paths ["test/clj" "integtest/clj"]

  :test-selectors {:unit (complement :integration)
                   :integration :integration
                   :all (constantly true)}

  :main {{ns}}.core

  :uberjar-name "{{name}}.jar"

  :aliases {"rel-jar" ["do" "clean," "cljsbuild" "once" "adv," "uberjar"]
            "unit" ["do" "test" ":unit"]
            "integ" ["do" "test" ":integration"]})
