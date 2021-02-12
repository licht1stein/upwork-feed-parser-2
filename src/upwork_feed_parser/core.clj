(ns upwork-feed-parser.core
  (:require [taoensso.carmine :as car :refer (wcar)]
            [upwork-feed-parser.config :as c]
            [upwork-feed-parser.rss.core :as rss]
            [upwork-feed-parser.telegram.core :as tg])
  (:gen-class))

(def conn {:pool {} :spec {:uri c/redis-url}})
(defmacro wcar* [& body] `(car/wcar conn ~@body))

(defn in? [x coll] (some #{x} coll))
(def not-in? (complement in?))

(defn get-checked-ids []
  (let [checked-ids (wcar* (car/get "checked-ids"))]
    (if (nil? checked-ids) [] checked-ids)))

(defn set-checked-ids [ids]
  (wcar*
    (car/set "checked-ids" (into [] ids))))

(defn runner [feed-ids checked-ids & pred-fs]
  (let [checked-ids (if-not checked-ids [] checked-ids)
        feed-urls (into [] (map rss/make-feed-url feed-ids))
        parsed-feed (into [] (rss/get-and-parse-feeds feed-urls))
        new-entries (filter #(not-in? (:id %) checked-ids) parsed-feed)
        new-checked-ids (set (concat checked-ids (map :id parsed-feed)))
        filtered-new-entries (filter (reduce every-pred pred-fs) new-entries)]
    {:checked-ids new-checked-ids :new-entries filtered-new-entries}))

(defn -main
  [& args]
  (while true
    (let [old-checked-ids (get-checked-ids)
          result (runner c/rss-feed-ids old-checked-ids rss/hourly?)
          new-checked-ids (into [] (:checked-ids result))
          formatted-entries (into [] (map rss/format-entry (:new-entries result)))]
      (println "New entries: " (count (:new-entries result)))
      (println (str "Checked ids: " (count old-checked-ids) " -> " (count new-checked-ids)))
      (set-checked-ids new-checked-ids)
      (tg/send-multiple-messages c/bot-token c/telegram-chat-id formatted-entries)
      (println "Sleep: " c/sleep-between-runs)
      (Thread/sleep c/sleep-between-runs))))

