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

(defn post-tagged?
  [post tag]

  (some #(= tag %1) (:tags post)))

(defn posts-tagged
  [posts tag]

  (filter #(post-tagged? %1 tag) posts))

(defn group-blog-by-tags
  [blog]

  (reduce #(assoc %1 %2 (posts-tagged blog %2)) {} (tags blog)))
