(ns upwork-feed-parser.core
  (:require [upwork-feed-parser.config :as c]
            [upwork-feed-parser.rss.core :as rss])
  (:gen-class))

;(def feed-urls (map rss/make-feed-url c/rss-feed-ids))
;(def parsed-feed (into [] (rss/get-and-parse-feeds feed-urls)))

(def state ^:private {})

(defn in? [x coll] (some #{x} coll))
(def not-in? (complement in?))

(defn send-to-telegram [entries]
  ;; Print for now
  (let [formatted (map rss/format-entry entries)]
    (map println formatted)))

(defn runner [feed-ids checked-ids & pred-fs]
  (let [feed-urls (into [] (map rss/make-feed-url feed-ids))
        parsed-feed (into [] (rss/get-and-parse-feeds feed-urls))
        new-entries (filter #(not-in? (:id %) checked-ids) parsed-feed)
        new-checked-ids (set (concat checked-ids (map :id parsed-feed)))
        filtered-new-entries (filter (reduce every-pred pred-fs) new-entries)]
        ;formatted-new-entries (map rss/format-entry filtered-new-entries)]
    {:checked-ids new-checked-ids :new-entries filtered-new-entries}))


(defn -main
  [& args]
  (loop []
    (let [result (runner c/rss-feed-ids (:checked-ids state) rss/hourly?)
          checked-ids (into [] (:checked-ids result))]
      (reset! state {:checked-ids checked-ids})
      (send-to-telegram (:new-entries result))
      (Thread/sleep 500))))

