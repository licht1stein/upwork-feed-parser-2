(ns upwork-feed-parser.rss.test_predicates
  (:require [clojure.test :refer [deftest is use-fixtures]]
            [expectations.clojure.test
             :refer [defexpect expect expecting
                     approximately between between' functionally
                     side-effects]]
            [upwork-feed-parser.rss.predicates :refer :all]))

(defexpect test-budget-above?
  (expect (budget-above? {:budget 80} 81 ) false)
  (expect (budget-above? {:budget '(25 40)} 35) true)
  (expect (budget-above? {:budget '(25 40)} 41) false)
  (expect (budget-above? {:budget 100} 100) true))

(defexpect test-spread-limited?
  (expect (spread-limited? {:budget 100} 0.2) true)
  (expect (spread-limited? {:budget '(80 100)} 0.2) true)
  (expect (spread-limited? {:budget '(80 100)} 0.19) false))

(defexpect test-hourly?
  (expect (hourly? {:hourly true}) true)
  (expect (hourly? {:hourly false}) false))

(defexpect test-hourly-or-high-fixed-budget?
  (expect (hourly-or-high-fixed-budget? {:hourly true :budget '(10 20)} 1000) true)
  (expect (hourly-or-high-fixed-budget? {:hourly false :budget 500} 1000) false)
  (expect (hourly-or-high-fixed-budget? {:hourly false :budget 1000} 1000) true))

