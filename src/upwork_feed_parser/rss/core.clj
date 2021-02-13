(ns upwork-feed-parser.rss.core
  (:require [feedparser-clj.core :refer [parse-feed]]
            [jsoup.soup :as soup]
            [clojure.string :as s]
            [upwork-feed-parser.rss.parsers :refer [parse-meta]]
            [upwork-feed-parser.config :as c]
            [feedparser-clj.core :as fp]
            [upwork-feed-parser.rss.helpers :refer [make-feed-url get-job-id-from-url]]
            [upwork-feed-parser.rss.predicates :refer :all]
            [upwork-feed-parser.rss.formatters :refer :all]))

(defn get-feed
  ([url] (:entries (fp/parse-feed url))))

(defn get-multiple-raw-feeds [urls]
  (flatten (map get-feed urls)))

(defn parse-entry-body [entry]
  (let [parsed (soup/parse (:value (first (:contents entry))))
        meta-tags (re-seq #"<b>[\w\s]+</b>:[\w\s].*" (.html parsed))
        parsed-meta-tags (map #(.text (soup/parse %)) meta-tags)
        final-meta-map (into {} (map parse-meta parsed-meta-tags))]
    (assoc final-meta-map :body (format-body-text parsed))))

(defn parse-entry [entry]
  (let [parsed-body (parse-entry-body entry)
        budget (if (:budget parsed-body) {} {:budget 0})
        title {:title (s/replace (:title entry) " - Upwork" "")}
        base {:title (:title entry)
              :link (:link entry)
              :date (:published-date entry)
              :id (get-job-id-from-url (:link entry))}
        res (merge base parsed-body budget title)]
    res))

(defn get-and-parse-feeds [urls]
  (map parse-entry (get-multiple-raw-feeds urls)))




