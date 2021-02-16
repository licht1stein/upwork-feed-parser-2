(ns upwork-feed-parser.rss.predicates
  (:require [clojure.string :as s]
            [upwork-feed-parser.config :as c]
            [upwork-feed-parser.utils :refer :all]
            [clojure.set :as set]))


(defn budget-above?
  ([entry target]
   (let [budget (:budget entry)]
     (if (seq? budget)
       (>= (last budget) target)
       (>= budget target)))))

(defn spread-limited?
  ([record]
   (spread-limited? record c/max-spread))
  ([record max-spread-percentage]
   (let [budget (:budget record)]
     (if (seq? budget)
       (<= (- 1 (/ (first budget) (last budget))) max-spread-percentage)
       true))))

(defn hourly? [entry]
  (:hourly entry))

(defn hourly-or-high-fixed-budget?
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
