(ns upwork-feed-parser.utils)

(defn in? [x coll] (some #{x} coll))
(def not-in? (complement in?))
