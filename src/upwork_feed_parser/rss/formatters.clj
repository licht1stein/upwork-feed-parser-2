(ns upwork-feed-parser.rss.formatters
  (:require [clojure.string :as s]
            [jsoup.soup :as soup]
            [selmer.parser :refer [render]]))


(defn format-body-text [body]
  (let [body (soup/select "body" body)
        lines (-> body
                  (s/replace "<body>" "")
                  (s/replace "</body>" "")
                  (s/replace "<br>" "\n")
                  (s/replace "<b>" "")
                  (s/replace "</b>" "")
                  (s/replace "  " " ")
                  (s/replace "&nbsp;" " ")
                  (s/replace "&amp;" "&")
                  (s/replace "&#39;" "'")
                  (s/split #"Hourly Range:|Budget:|Posted On:")
                  (first)
                  (s/split-lines))
        non-empty-lines (map s/trim (filter #(re-find #"\S" %) lines))]
    (s/join "\n\n" non-empty-lines)))

(def entry-template
  "
<b>{{title}}</b>
{{country}} | {{posted-on}}
Budget: {{budget}}

Feed: {{feed-name}}
Category: {{category}}

Skills: {{skills}}

{{body}}

<a href=\"{{link}}\">Link</a>")

(defn format-budget [budget]
  (if (seq? budget)
    (s/join "-" budget)
    (pr-str budget)))

(defn format-entry [entry]
  (let [budget (format-budget (:budget entry))
        skills (if (:skills entry) (s/join ", " (:skills entry)) "N/A")
        body (if (:body entry) (:body entry) "N/A")
        rendered (render entry-template (assoc entry :budget budget :skills skills :body body))]
    rendered))
