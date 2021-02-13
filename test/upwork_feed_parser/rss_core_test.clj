(ns upwork-feed-parser.rss-core-test
  (:require [clojure.test :refer [deftest is use-fixtures]]
            [expectations.clojure.test
             :refer [defexpect expect expecting
                     approximately between between' functionally
                     side-effects]]
            [clojure.edn :as edn]
            [upwork-feed-parser.rss.core :refer :all]))

(def data (edn/read-string (slurp "test/test_feed.edn")))
(def parsed-data (edn/read-string (slurp "test/parsed_test_feed.edn")))

(defexpect make-feed-url-test
  (expect (make-feed-url "token" 666 555 444) "https://www.upwork.com/ab/feed/topics/rss?securityToken=token&userUid=666&orgUid=555&topic=444"))

(defexpect trim-job-url-test
  (expect (trim-job-url "https://www.upwork.com/jobs/Clonate-existing-telegram-bot-functions_%7E017018ec4aaef95cc1?source=rss") "7E017018ec4aaef95cc1"))

(defexpect parse-entry-body-test
  (expect (count (map parse-entry-body data)) (count data))
  (expect (keys (parse-entry-body (first data))) '(:hourly :budget :posted-on :category :skills :country :body))
  (expect (parse-entry-body (last data)) {:hourly true
                                          :budget '(15.0 30.0)
                                          :posted-on "February 10, 2021 06:43 UTC"
                                          :category "Back-End Development"
                                          :skills '("Python" "Data Science" "Natural Language Processing")
                                          :country "India"
                                          :body "Selected intern's day-to-day responsibilities include: 1. Understand requirements and develop solutions 2. Work on unit testing and defect fixing 3. Build services in Python with NLP capabilities 4. Deliver allocated task as per agreed timelines 5. Attend team meetings for brainstorming and status update Other requirements 1. Strong knowledge of Python and general software development skills(Git, VS code, testing) 2. Knowledge of NLP packages - NLTK, Gensim, CoreNLP, spaCy 3. Knowledge of Python API development using Flask/Django 4. Knowledge of ML/math toolkit - NumPy 5. Understanding on NLP algorithms "}))

(defexpect parse-entry-test
  (expect (count (map parse-entry data)) (count data))
  (expect (map parse-entry data) parsed-data)
  (expect (parse-entry (first data)) {:category "Full Stack Development"
                                      :date #inst "2021-02-10T16:42:37.000-00:00"
                                      :title "Clonate a existing telegram bot functions - Upwork"
                                      :skills '("API" "Database Architecture" "Web Design" "Website Development" "Web Application" "Java" "Kotlin" "JavaScript" "PHP" "Ruby" ".NET Framework" "SQLite" "NGINX" "Very Small (1-9 employees)" "MySQL" "API Integration" "React" "Node.js" "Python" "UX/UI")
                                      :hourly true
                                      :link "https://www.upwork.com/jobs/Clonate-existing-telegram-bot-functions_%7E017018ec4aaef95cc1?source=rss"
                                      :id "7E017018ec4aaef95cc1"
                                      :budget '(10.0 25.0)
                                      :posted-on "February 10, 2021 16:42 UTC"
                                      :body "Hello, I need someone expert in Telegram APIs to create an advaced telegram bot All the functions would be replicated from a existing telegram bot. Please send your proposal, I will send more info at private chat. "
                                      :country "Spain"})
  (expect (parse-entry (second data)) {:category "Scripting & Automation"
                                       :date #inst "2021-02-10T13:04:28.000-00:00"
                                       :title "Create Telegram Bot - Upwork"
                                       :skills '("Telegram API" "Bot Development" "Python" "Chatbot Development")
                                       :hourly false
                                       :link "https://www.upwork.com/jobs/Create-Telegram-Bot_%7E019a982f16666100aa?source=rss"
                                       :id "7E019a982f16666100aa"
                                       :budget 80.0
                                       :posted-on "February 10, 2021 13:04 UTC"
                                       :body "Hello, I want to create simple FAQ Telegram bot with simple tickets option for technical support. Let me know, if you're interesting. ", :country "Russia"}))

(defexpect budget-above?-test
  (expect (budget-above? (first parsed-data) 25) true)
  (expect (budget-above? (first parsed-data) 26) false)
  (expect (budget-above? (second parsed-data) 80) true)
  (expect (budget-above? (second parsed-data) 81) false)
  (expect (budget-above? {:budget 80} 81) false))

(defexpect hourly?-test
  (expect (hourly? (first parsed-data)) true)
  (expect (hourly? (second parsed-data)) false))

(defexpect test-formatter
  (expect (format-entry (first parsed-data))) nil)

(defexpect test-make-body-html
  (expect (:body (parse-entry-body (first data))) nil))