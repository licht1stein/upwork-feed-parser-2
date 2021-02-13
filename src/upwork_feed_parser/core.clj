(ns upwork-feed-parser.core
  (:require [taoensso.carmine :as car :refer (wcar)]
            [upwork-feed-parser.config :as c]
            [upwork-feed-parser.rss.core :as rss]
            [upwork-feed-parser.rss.predicates :as p]
            [upwork-feed-parser.rss.formatters :as f]
            [upwork-feed-parser.telegram.core :as tg]
            [upwork-feed-parser.utils :refer :all]
            [upwork-feed-parser.state.core :refer [get-checked-ids set-checked-ids]])
  (:gen-class))

(defn runner [checked-ids & pred-fs]
  (let [checked-ids (if-not checked-ids [] checked-ids)
        parsed-feed (into [] (rss/get-and-parse-feeds))
        new-entries (filter #(not-in? (:id %) checked-ids) parsed-feed)
        new-checked-ids (set (concat checked-ids (map :id parsed-feed)))
        filtered-new-entries (filter (reduce every-pred pred-fs) new-entries)]
    {:checked-ids new-checked-ids :new-entries filtered-new-entries}))

(defn -main
  [& args]
  (while true
    (let [old-checked-ids (get-checked-ids)
          result (runner old-checked-ids
                         #(p/hourly-or-high-fixed-budget? % c/min-fixed-budget)
                         #(p/budget-above? % c/min-hourly-budget)
                         p/country-not-blacklisted?
                         p/no-stop-words?)
          new-checked-ids (into [] (:checked-ids result))
          formatted-entries (into [] (map f/format-entry (:new-entries result)))]
      (println "New entries: " (count (:new-entries result)))
      (println (str "Checked ids: " (count old-checked-ids) " -> " (count new-checked-ids)))
      (set-checked-ids new-checked-ids)
      (tg/send-multiple-messages c/bot-token c/telegram-chat-id formatted-entries)
      (println "Sleep: " c/sleep-between-runs)
      (Thread/sleep c/sleep-between-runs))))
