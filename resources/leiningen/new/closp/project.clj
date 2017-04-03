(defproject {{name}} "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :source-paths ["src/clj" "src/cljs" "src/cljc"]

  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                 [org.clojure/clojurescript "1.9.293"]

                 [org.clojure/core.cache "0.6.5"]
                 [org.clojure/core.async "0.2.395"]

                 [ring "1.5.1"]
                 [lib-noir "0.9.9"]
                 [ring/ring-anti-forgery "1.0.1"]
                 [compojure "1.5.2"]
                 [reagent "0.6.0"]
                 [org.immutant/web "2.1.6"]
                 [hiccup "1.0.5"]
                 [prone "1.1.4"]
                 [im.chit/hara.io.scheduler "2.4.7"]
                 [noir-exception "0.2.5"]

                 [buddy/buddy-auth "1.3.0"]
                 [buddy/buddy-hashers "1.1.0"]

                 [log4j "1.2.17" :exclusions [javax.mail/mail
                                              javax.jms/jms
                                              com.sun.jdmk/jmxtools
                                              com.sun.jmx/jmxri]]

                 [com.draines/postal "2.0.2"]

                 [de.sveri/clojure-commons "0.2.0"]

                 [org.clojure/tools.namespace "0.2.11"]
                 [org.danielsz/system "0.3.2-SNAPSHOT"]

                 [cljs-ajax "0.5.8"]
                 [ring-transit "0.1.6"]
                 [com.lucasbradstreet/cljs-uuid-utils "1.0.2"]

                 [net.tanesha.recaptcha4j/recaptcha4j "0.0.8"]

                 [com.taoensso/tempura "1.0.0"]


                 [prismatic/plumbing "0.5.3"]
                 [prismatic/schema "1.1.3"]

                 [fipp "0.6.8"]
                 [com.rpl/specter "0.13.2"]

                 [de.sveri/closp-crud "0.3.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [org.postgresql/postgresql "9.4.1212"]
                 [org.clojure/java.jdbc "0.6.2-alpha3"]]

  :plugins [[lein-cljsbuild "1.1.1"]]

  :min-lein-version "2.5.0"

  ; leaving this commented because of: https://github.com/cursiveclojure/cursive/issues/369
  ;:hooks [leiningen.cljsbuild]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :cljsbuild
  {:builds {:dev {:source-paths ["src/cljs" "src/cljc" "env/dev/cljs"]
                  :figwheel {:css-dirs ["resources/public/css"]             ;; watch and update CSS
                             :on-jsload "{{name}}.dev/main"}
                  :compiler     {:main           "{{name}}.dev"
                                 :asset-path     "/js/compiled/out"
                                 :output-to      "resources/public/js/compiled/app.js"
                                 :output-dir     "resources/public/js/compiled/out"}}
            :adv {:source-paths ["src/cljs" "src/cljc"]
                  :compiler     {:output-to     "resources/public/js/compiled/app.js"
                                 ; leaving this commented because of: https://github.com/cursiveclojure/cursive/issues/369
                                 ;:jar           true
                                 :optimizations :advanced
                                 :pretty-print  false}}}}

  :profiles {:dev     {:repl-options {:init-ns          {{ns}}.user}

                       :plugins      [[lein-ring "0.9.0"]
                                      [lein-figwheel "0.5.0-2"]
                                      [test2junit "1.1.1"]]

                       :dependencies [[org.apache.httpcomponents/httpclient "4.5.2"]
                                      [clj-webdriver "0.7.2"]
                                      [org.seleniumhq.selenium/selenium-java "2.52.0"]

                                      [org.clojure/test.check "0.9.0"]

                                      [clojure-miniprofiler "0.5.0"]

                                      [ring/ring-devel "1.5.1"]
                                      [pjstadig/humane-test-output "0.8.1"]]

                       :injections   [(require 'pjstadig.humane-test-output)
                                      (pjstadig.humane-test-output/activate!)]}

             :uberjar {:auto-clean false                    ; not sure about this one
                       :omit-source true
                       :aot         :all}}

  :test-paths ["test/clj" "integtest/clj"]

  :test-selectors {:unit        (fn [m] (not (or (:selenium m) (:integration m) (:rest m))))
                   :integration :integration
                   :selenium    :selenium
                   :rest        :rest
                   :cur         :cur                                ; one more selector for, give it freely to run only
                   ; the ones you need currently
                   :all         (constantly true)}

  :test2junit-output-dir "test-results"

  :main {{ns}}.core

  :uberjar-name "{{name}}.jar"

  :aliases {"rel-jar" ["do" "clean," "cljsbuild" "once" "adv," "uberjar"]
            "unit" ["do" "test" ":unit"]
            "integ" ["do" "test" ":integration"]}


  :test-refresh {:quiet true
                 :changes-only true})
