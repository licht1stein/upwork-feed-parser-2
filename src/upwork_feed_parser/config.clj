(ns upwork-feed-parser.config
  (:require [clojure.string :as s]))

(defn env
  ([key]
   (let [val (System/getenv key)]
     (if (nil? val)
       (throw (AssertionError. (str "Environment variable not set: " key)))
       val)))
  ([key default]
   (or (System/getenv key) default)))

(defn env-and-cast
  ([key cast-fn]
   (let [val (env key)]
     (cast-fn val)))
  ([key default cast-fn]
   (let [val (env key default)]
     (cond
       (nil? val) default
       (= val default) default
       :default (cast-fn val)))))

(defn env-cast-maker [cast-fn]
  (fn
    ([key]
     (env-and-cast key cast-fn))
    ([key default]
     (env-and-cast key default cast-fn))))

(def env-int (env-cast-maker #(Integer/parseInt %)))
(def env-vec (env-cast-maker #(s/split % #" ")))

(defonce security-token (env "UPWORK_SECURITY_TOKEN"))
(defonce user-id (env "UPWORK_USER_ID"))
(defonce org-uid (env "UPWORK_USER_ID"))
(defonce bot-token (env "BOT_TOKEN"))
(defonce telegram-chat-id (env-int "TELEGRAM_CHAT_ID"))
(def rss-feed-ids (env-vec "UPWORK_FEED_IDS" []))
(def blacklisted-countries (env-vec "EXCLUDED_COUNTRIES" []))
(def min-hourly-budget (env-int "MIN_HOURLY_BUDGET" 45))
(def min-fixed-budget (env-int "MIN_FIXED_BUDGET" nil))
(def redis-url (env "REDIS_URL" nil))
(def sleep-between-runs (env-int "SLEEP_BETWEEN_RUNS" 1000))
(def time-zone (env "TZ" "Europe/Kiev"))
