(ns upwork-feed-parser.telegram.core
  (:require [clj-http.client :as client]))

(defn send-message [bot-token chat-id text]
  (let [url (str "https://api.telegram.org/bot" bot-token "/sendMessage")
        params {:chat_id                  chat-id
                :text                     text
                :parse_mode               "HTML"
                :disable_web_page_preview true}]
    (client/post url {:form-params params})))

(defn send-multiple-messages [bot-token chat-id messages]
  (if-not (empty? messages)
    (do
      (send-message bot-token chat-id (first messages))
      (Thread/sleep 500)
      (recur bot-token chat-id (rest messages)))))