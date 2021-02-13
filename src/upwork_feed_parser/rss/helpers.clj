(ns upwork-feed-parser.rss.helpers
  (:require [upwork-feed-parser.config :as c]))

(defn make-feed-url
  ([topic-id]
   (make-feed-url c/security-token c/user-id c/org-uid topic-id))
  ([security-token user-id feed-id topic-id]
   (format "https://www.upwork.com/ab/feed/topics/rss?securityToken=%s&userUid=%s&orgUid=%s&topic=%s"
           security-token user-id feed-id topic-id)))

(defn get-job-id-from-url [url]
  (re-find #"(?<=%)\w+(?=\?)" url))
