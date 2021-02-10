(ns upwork-feed-parser.core
  (:require [upwork-feed-parser.config :as c]
            [upwork-feed-parser.rss.core :as rss])
  (:gen-class))



(defn runner [feed-ids checked-ids & pred-fs]
  (let [feed-urls (into [] (map rss/make-feed-url feed-ids))
        parsed-feed (into [] (rss/get-and-parse-feeds feed-urls))
        new-entries (filter #(some #{(:id %)} checked-ids) parsed-feed)
        new-checked-ids (conj checked-ids (map :id parsed-feed))
        filtered-new-entries (rss/filter-parsed-feed new-entries pred-fs)]
    (println filtered-new-entries)
    (Thread/sleep 1000)
    (recur feed-ids new-checked-ids pred-fs)))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]

  (runner c/rss-feed-ids [] rss/hourly?))
