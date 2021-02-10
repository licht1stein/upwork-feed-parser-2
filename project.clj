(defproject upwork-feed-parser "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojars.scsibug/feedparser-clj "0.4.0"]
                 [clj-soup/clojure-soup "0.1.3"]
                 [expectations/clojure-test "1.2.1"]]
  :main ^:skip-aot upwork-feed-parser.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}}
  :plugins [[lein-expectations "0.0.8"]])