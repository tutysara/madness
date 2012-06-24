(ns madness.utils
  (:require [clojure.string :as str]
            [clj-time.format :as time-format]))

(defn tags
  [blog]

  (apply sorted-set (mapcat :tags blog)))

(defn tag-to-url [tag]
  (str "/blog/tags/" (str/replace (str/lower-case tag) " " "-") "/"))

(defn date-format
  [date]

  (time-format/unparse (time-format/formatter "YYYY-MM-dd") date))
