(ns upwork-feed-parser.config
  (:require [clojure.string :as s]))

(defn env
  ([key]
   (env key nil))
  ([key default]
   (let [val (or (System/getenv key) default)]
     (if (nil? val)
       val
       (throw (AssertionError. (str "Environment variable not set: " key)))))))

(defn env-and-cast
  ([key cast-fn]
   (env-and-cast key nil cast-fn))
  ([key default cast-fn]
   (let [val (env key default)]
     (if (= val default)
       default
       (cast-fn val)))))

(defn env-cast-maker [cast-fn]
  (fn
    ([key]
     (env-and-cast key nil cast-fn))
    ([key default]
     (env-and-cast key default cast-fn))))

(def env-int (env-cast-maker #(Integer/parseInt %)))
(def env-vec (env-cast-maker #(s/split % #" ")))

(defonce security-token (env "UPWORK_SECURITY_TOKEN"))
(defonce user-id (env "UPWORK_USER_ID"))
(defonce org-uid (env "UPWORK_USER_ID"))
(defonce bot-token (env "BOT_TOKEN"))
(def rss-feed-ids (env-vec "UPWORK_FEED_IDS"))
(def blacklisted-countries (env-vec "EXCLUDED_COUNTRIES"))
(def min-hourly-budget (env-int "MIN_HOURLY_BUDGET" 45))
(def min-fixed-budget (env-int "MIN_FIXED_BUDGET" 5000))
(def redis-url (env "REDIS_URL" "redis://localhost:6379/"))
(def sleep-between-runs (env-int "SLEEP_BETWEEN_RUNS" 1000))
(def bot-token (env "BOT_TOKEN"))
