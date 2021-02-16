(ns upwork-feed-parser.rss-core-test
  (:require [clojure.test :refer [deftest is use-fixtures]]
            [expectations.clojure.test
             :refer [defexpect expect expecting
                     approximately between between' functionally
                     side-effects]]
            [clojure.edn :as edn]
            [upwork-feed-parser.rss.core :refer :all]
            [upwork-feed-parser.rss.predicates :refer :all]
            [upwork-feed-parser.rss.formatters :refer :all]
            [upwork-feed-parser.rss.helpers :refer :all]))

(def data (edn/read-string (slurp "test/test_feed.edn")))
(def parsed-data (edn/read-string (slurp "test/parsed_test_feed.edn")))

(defexpect make-feed-url-test
  (expect (make-feed-url "token" 666 555 444) "https://www.upwork.com/ab/feed/topics/rss?securityToken=token&userUid=666&orgUid=555&topic=444"))

(defexpect get-job-id-from-url-test
  (expect (get-job-id-from-url "https://www.upwork.com/jobs/Clonate-existing-telegram-bot-functions_%7E017018ec4aaef95cc1?source=rss") "7E017018ec4aaef95cc1"))

(defexpect parse-entry-body-test
  (expect (count (map parse-entry-body data)) (count data))
  (expect (keys (parse-entry-body (first data))) '(:hourly :budget :posted-on :category :skills :country :body)))

(defexpect parse-entry-test
  (expect (count (map parse-entry data)) (count data))
  (expect (map parse-entry data) parsed-data))

(defexpect budget-above?-test
  (expect (budget-above? {:budget 80} 81 ) false)
  (expect (budget-above? {:budget '(25 40)} 35) true)
  (expect (budget-above? {:budget '(25 40)} 41) false)
  (expect (budget-above? {:budget 100} 100) true))

(defexpect test-spread-limited?
  (expect (spread-limited? {:budget 100} 0.2) true)
  (expect (spread-limited? {:budget '(80 100)} 0.2) true)
  (expect (spread-limited? {:budget '(80 100)} 0.19) false))

(defexpect hourly?-test
  (expect (hourly? (first parsed-data)) true)
  (expect (hourly? (second parsed-data)) false))

(defexpect test-formatter
  (expect (format-entry (first parsed-data))) nil)
