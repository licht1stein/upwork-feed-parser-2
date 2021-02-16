(ns upwork-feed-parser.rss.test-parsers
  (:require [clojure.test :refer [deftest is]]
            [upwork-feed-parser.rss.parsers :refer :all]
            [expectations.clojure.test
             :refer [defexpect expect expecting
                     approximately between between' functionally
                     side-effects]]))

(defexpect test-trimmed-or-nil
  (expect (trimmed-or-nil " foo ") "foo")
  (expect (trimmed-or-nil "") nil))

(defexpect test-dispatch-meta
  (expect (dispatch-meta "Budget: $300") :budget)
  (expect (dispatch-meta "Hourly Range: $25.00-$50.00") :hourly)
  (expect (dispatch-meta "Country: India") :country)
  (expect (dispatch-meta "Skills: Python, Clojure") :skills)
  (expect (dispatch-meta "Category: Back-End") :category))

(defexpect test-parse-meta-budget
  (expect (parse-meta "Budget: $300") {:hourly false :budget 300.0})
  (expect (parse-meta "Budget: ") {:hourly false :budget 0}))

(defexpect test-parse-meta-hourly
  (expect (parse-meta "Hourly Range: $25.00-$50.00") {:hourly true :budget '(25.0 50.0)})
  (expect (parse-meta "Hourly: $50.00") {:hourly true :budget '(50.0)}))

(defexpect test-parse-meta-country
  (expect (parse-meta "Country: India") {:country "India"})
  (expect (parse-meta "Country:") {:country nil}))

(defexpect test-parse-meta-skills
  (expect (parse-meta "Skills: Python, Clojure") {:skills ["Python" "Clojure"]})
  (expect (parse-meta "Skills:") {:skills []}))

(defexpect test-parser-meta-category
  (expect (parse-meta "Category: Back-End Development") {:category "Back-End Development"})
  (expect (parse-meta "Category:") {:category nil}))