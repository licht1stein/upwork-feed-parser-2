(defproject upwork-feed-parser "0.1.0-SNAPSHOT"
  :description "Upwork Feed Parser for Telegram"
  :url "https://github.com/licht1stein/upwork-feed-parser-2"
  :min-lein-version "2.0.0"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [remus "0.2.1"]
                 [clj-soup/clojure-soup "0.1.3"]
                 [expectations/clojure-test "1.2.1"]
                 [selmer "1.12.33"]
                 [com.taoensso/carmine "3.1.0"]
                 [clj-http "3.12.0"]]
  :jar-name "upwork-feed-parser.jar"
  :jar-path "/target/upwork-feed-parser.jar"
  :main ^:skip-aot upwork-feed-parser.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot      :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
