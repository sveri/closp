(defproject closp/lein-template "0.3.0-alpha4"
  :description "A webframework combining several technologies based on luminus and chestnut."
  :url "https://github.com/sveri/closp"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[leinjacker "0.2.0"]
                 [leiningen-core "2.5.1"]
                 [leiningen "2.5.1"]
                 [org.clojure/tools.cli "0.3.1"]]
  :test-paths ["test/clj"]
  :eval-in-leiningen true
  :deploy-repositories [["clojars-self" {:url           "https://clojars.org/repo"
                                         :sign-releases false}]])