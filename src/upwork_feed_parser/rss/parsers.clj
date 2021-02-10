(ns upwork-feed-parser.rss.parsers
  (:require [clojure.string :as s]))

(defn trimmed-or-nil [str]
  (let [trimmed (s/trim str)]
    (if (> (count trimmed) 0)
      trimmed
      nil)))

(defn dispatch-meta [property]
  (cond
    (s/starts-with? property "Budget") :budget
    (s/starts-with? property "Hourly") :hourly
    (s/starts-with? property "Country") :country
    (s/starts-with? property "Skills") :skills
    (s/starts-with? property "Category") :category
    (s/starts-with? property "Posted") :posted-on
    :default :unknown))

(defmulti parse-meta dispatch-meta)

(defmethod parse-meta :budget [prop]
  (let [budget (re-find #"\d+" prop)]
    {:hourly false
     :budget (if budget
               (Double/parseDouble budget)
               0)}))

(defmethod parse-meta :hourly [prop]
  (let [hourly-range (re-seq #"\d+.\d\d" prop)]
    (if (seq? hourly-range)
      {:hourly true
       :budget (map #(Double/parseDouble %) hourly-range)})))

(defmethod parse-meta :country [prop] {:country (trimmed-or-nil (s/replace prop "Country:" ""))})

(defmethod parse-meta :skills [prop]
  (let [skills (s/split (s/replace prop "Skills:" "") #",")
        trimmed-skills (map s/trim skills)
        non-empty-skills (filter #(> (count %) 0) trimmed-skills)]
    {:skills non-empty-skills}))

(defmethod parse-meta :category [prop] {:category (trimmed-or-nil (s/replace prop "Category:" ""))})

(defmethod parse-meta :posted-on [prop] {:posted-on (trimmed-or-nil (s/replace prop "Posted On:" ""))})

(defmethod parse-meta :unknown [prop]
  (throw (Exception. (str "Failed to categorize: " prop))))
