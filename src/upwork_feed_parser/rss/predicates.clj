(ns upwork-feed-parser.rss.predicates
  (:require [clojure.string :as s]
            [upwork-feed-parser.config :as c]
            [upwork-feed-parser.utils :refer :all]
            [clojure.set :as set]))


(defn budget-above?
  "Checks that the budget is above target. For fixed budget does a simple >= comparison. For hourly budgets compares the
  biggest value."
  ([entry target]
   (let [budget (:budget entry)]
     (if (seq? budget)
       (>= (reduce max budget) target)
       (>= budget target)))))

(defn spread-limited?
  "Checks that the spread on an hourly budget is limited to the max-spread-percentage. If only one argument is provided,
  takes the max-spread-percentage from config/max-spread environment variable."
  ([record]
   (spread-limited? record c/max-spread))
  ([record max-spread-percentage]
   (let [budget (:budget record)]
     (if (seq? budget)
       (<= (- 1 (/ (first budget) (last budget))) max-spread-percentage)
       true))))

(defn hourly? [entry]
  "Checks if the job is hourly."
  (:hourly entry))

(defn hourly-or-high-fixed-budget?
  "Checks if the job is hourly or that it has a fixed budget >= to high-budget. If no high-budget is provided, takes it
  from environment variable config/min-fixed-budget"
  ([entry]
   (hourly-or-high-fixed-budget? entry c/min-fixed-budget))
  ([entry high-budget]
   (or (hourly? entry) (budget-above? entry high-budget))))

(defn country-not-blacklisted? [entry]
  (not-in? (:country entry) c/blacklisted-countries))

(defn no-stop-words?
  ([entry]
   (no-stop-words? entry c/stop-words))
  ([entry stop-words]
   (let [entry-words (concat (s/split (:body entry) #" ") (s/split (:title entry) #" "))]
     (empty? (set/intersection (set stop-words) (set entry-words))))))

(defn skill-count-below? [entry max-skills]
  (<= (count (:skills entry)) max-skills))

(defn filter-parsed-feed [entries & pred-funcs]
  (filter (reduce every-pred pred-funcs)  entries))
