(ns upwork-feed-parser.rss.core
  (:require [feedparser-clj.core :refer [parse-feed]]
            [jsoup.soup :as soup]
            [clojure.string :as s]
            [upwork-feed-parser.rss.parsers :refer [parse-meta]]
            [upwork-feed-parser.config :as c]
            [feedparser-clj.core :as fp]))


(defn make-feed-url
  ([topic-id]
   (make-feed-url c/security-token c/user-id c/org-uid topic-id))
  ([security-token user-id feed-id topic-id]
   (format "https://www.upwork.com/ab/feed/topics/rss?securityToken=%s&userUid=%s&orgUid=%s&topic=%s"
           security-token user-id feed-id topic-id)))

(defn get-feed
  ([url] (:entries (fp/parse-feed url))))

(defn get-multiple-raw-feeds [urls]
  (flatten (map get-feed urls)))

(defn trim-job-url [url]
  (re-find #"(?<=%)\w+(?=\?)" url))

(defn remove-metadata [body]
  (re-find #".+?(?=Hourly Range:|Budget:)" body))

(defn parse-entry-body [entry]
  (let [parsed (soup/parse (:value (first (:contents entry))))
        meta-tags (re-seq #"<b>[\w\s]+</b>:[\w\s].*" (.html parsed))
        parsed-meta-tags (map #(.text (soup/parse %)) meta-tags)
        final-meta-map (into {} (map parse-meta parsed-meta-tags))]
    (assoc final-meta-map :body (remove-metadata (.text parsed)))))

(defn parse-entry [entry]
  (let [parsed-body (parse-entry-body entry)
        base {:title (:title entry)
              :link (:link entry)
              :date (:published-date entry)
              :id (trim-job-url (:link entry))}]
    (merge base parsed-body)))

(defn get-and-parse-feeds [urls]
  (map parse-entry (get-multiple-raw-feeds urls)))

(defn budget-above? [entry target]
  (let [budget (:budget entry)]
    (if (seq? budget)
      (>= (last budget) target)
      (>= budget target))))

(defn hourly? [entry]
  (:hourly entry))

(defn hourly-or-high-fixed-budget?
  ([entry high-budget]
   (or (hourly? entry) (budget-above? entry high-budget))))

(defn country-not-blacklisted? [entry]
  (not (some #(= (:country entry) %) c/blacklisted-countries)))

(defn filter-parsed-feed [entries & pred-funcs]
  (filter (reduce every-pred pred-funcs)  entries))

