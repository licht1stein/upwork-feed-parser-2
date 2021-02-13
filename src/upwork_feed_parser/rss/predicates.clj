(ns upwork-feed-parser.rss.predicates
  (:require [upwork-feed-parser.config :as c]))

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

(defn skill-count-below? [entry max-skills]
  (<= (count (:skills entry)) max-skills))

(defn filter-parsed-feed [entries & pred-funcs]
  (filter (reduce every-pred pred-funcs)  entries))
