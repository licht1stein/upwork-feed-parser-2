(ns upwork-feed-parser.state.core
  (:require [taoensso.carmine :as car :refer (wcar)]
            [upwork-feed-parser.config :as c]))

(def ^:private state (atom {}))

(def conn {:pool {} :spec {:uri c/redis-url}})
(defmacro wcar* [& body] `(car/wcar conn ~@body))

(defn select-state-dispatcher
  ([_]
   (select-state-dispatcher))
  ([]
   (cond
     (not (nil? c/redis-url)) :redis
     :default :atom)))

(defmulti get-checked-ids select-state-dispatcher)

(defmethod get-checked-ids :redis []
  (let [checked-ids (wcar* (car/get "checked-ids"))]
    (if (nil? checked-ids) [] checked-ids)))

(defmethod get-checked-ids :atom []
  (or (:checked-ids @state) []))

(defmulti set-checked-ids select-state-dispatcher)

(defmethod set-checked-ids :redis [ids]
  (wcar*
    (car/set "checked-ids" (into [] ids))))

(defmethod set-checked-ids :atom [ids]
  (reset! state (assoc @state :checked-ids ids)))
