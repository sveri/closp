(defproject {{name}} "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :source-paths ["src/clj" "src/cljs" "src/cljc"]

  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.238"]

                 [org.clojure/core.cache "0.6.5"]
                 [org.clojure/core.async "0.3.465"]

                 [ch.qos.logback/logback-classic "1.2.3"]

                 [ring "1.6.3"]
                 [lib-noir "0.9.9"]
                 [ring/ring-anti-forgery "1.1.0"]
                 [compojure "1.6.0"]

                 [reagent "0.7.0"]
                 [re-frame "0.10.5"]
                 [secretary "1.2.3"]

                 [org.immutant/web "2.1.9"]
                 [hiccup "1.0.5"]
                 [prone "1.1.4"]
                 [im.chit/hara.io.scheduler "2.5.10"]
                 [noir-exception "0.2.5"]

                 [buddy/buddy-auth "2.1.0"]
                 [buddy/buddy-hashers "1.3.0"]

                 [com.draines/postal "2.0.2"]

                 [de.sveri/clojure-commons "0.2.2"]

                 [org.clojure/tools.namespace "0.2.11"]
                 [org.danielsz/system "0.4.1"]

                 [cljs-ajax "0.7.3"]
                 [ring-transit "0.1.6"]

                 [net.tanesha.recaptcha4j/recaptcha4j "0.0.8"]

                 [com.taoensso/tempura "1.1.2"]


                 [prismatic/plumbing "0.5.5"]

                 [fipp "0.6.12"]
                 [com.rpl/specter "1.0.5"]

                 [org.clojure/tools.logging "0.4.0"]

                 [org.postgresql/postgresql "42.1.4"]
                 [org.clojure/java.jdbc "0.7.4"]
                 [com.mchange/c3p0 "0.9.5.2"]]

  :plugins [[lein-cljsbuild "1.1.7"]]

  :min-lein-version "2.5.0"

  ; leaving this commented because of: https://github.com/cursiveclojure/cursive/issues/369
  ;:hooks [leiningen.cljsbuild]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :cljsbuild {:builds
              [{:id "dev"
                :source-paths ["src/cljs" "src/cljc"]
                :figwheel {:on-jsload "{{name}}.core/mount-root"}

                :compiler {:main "{{name}}.core"
                           :asset-path "/js/compiled/out"
                           :output-to "resources/public/js/compiled/app.js"
                           :output-dir "resources/public/js/compiled/out"
                           :source-map-timestamp true
                           :preloads [devtools.preload
                                      day8.re-frame-10x.preload
                                      re-frisk.preload]
                           :closure-defines      {"re_frame.trace.trace_enabled_QMARK_" true}
                           :external-config      {:devtools/config {:features-to-install :all}}}}

               {:id "adv"
                :source-paths ["src/cljs" "src/cljc"]
                :compiler {:output-to "resources/public/js/compiled/app.js"
                           :optimizations :advanced
                           :pretty-print false
                           :infer-externs true
                           :closure-defines {goog.DEBUG false}}}]}

  :figwheel {:css-dirs ["resources/public/css"]}

  :profiles {:dev     {:repl-options {:init-ns          {{ns}}.user}

                       :plugins      [[lein-ring "0.9.0"]
                                      [lein-figwheel "0.5.14"]
                                      [test2junit "1.1.1"]
                                      [lein-doo "0.1.10"]]

                       :dependencies [[etaoin "0.2.2"]

                                      [org.clojure/test.check "0.9.0"]

                                      [ring/ring-devel "1.6.3"]
                                      [pjstadig/humane-test-output "0.8.3"]

                                      [binaryage/devtools "0.9.9"]
                                      [day8.re-frame/re-frame-10x "0.3.0"]
                                      [re-frisk "0.5.4"]]

                       :injections   [(require 'pjstadig.humane-test-output)
                                      (pjstadig.humane-test-output/activate!)]}

             :uberjar {:auto-clean false                    ; not sure about this one
                       :omit-source true
                       :aot         :all}}

  :test-paths ["test/clj" "integtest/clj"]

  :test-selectors {:unit        (fn [m] (not (or (:frontend m) (:integration m) (:rest m))))
                   :integration :integration
                   :frontend    :frontend
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
