(ns upwork-feed-parser.config
  (:require [clojure.string :as s]))

(defn get-env-with-cast-and-default
  ([key]
   (get-env-with-cast-and-default key identity nil))
  ([key cast-fn]
   (get-env-with-cast-and-default key cast-fn nil))
  ([key default cast-fn]
   (let [val (System/getenv key)]
     (if val
       (cast-fn val)
       default))))

(defn get-env-int
  ([env]
   (get-env-int env nil))
  ([env default]
   (get-env-with-cast-and-default env #(Integer/parseInt %) default)))

(defn get-env-vector
  ([env]
   (get-env-vector env []))
  ([env default]
   (get-env-with-cast-and-default env default #(s/split % #" "))))

(defn get-env
  ([key]
   (get-env key nil))
  ([key default]
   (or (System/getenv key) default)))

(defn get-env-or-throw [key]
  (let [val (get-env key)]
    (if val
      val
      (throw (AssertionError. (str "Environment variable not set: " key))))))

(defonce security-token (get-env-or-throw "UPWORK_SECURITY_TOKEN"))
(defonce user-id (get-env-or-throw "UPWORK_USER_ID"))
(defonce org-uid (get-env-or-throw "UPWORK_USER_ID"))
(defonce bot-token (get-env-or-throw "BOT_TOKEN"))
(def rss-feed-ids (get-env-vector "UPWORK_FEED_IDS"))
(def blacklisted-countries (get-env-vector "EXCLUDED_COUNTRIES"))
(def min-hourly-budget (get-env-int "MIN_HOURLY_BUDGET" 45))
(def min-fixed-budget (get-env-int "MIN_FIXED_BUDGET" 5000))
(def redis-url (get-env "REDIS_URL" "redis://localhost:6379/"))
(def sleep-between-runs (get-env-int "SLEEP_BETWEEN_RUNS" 1000))
