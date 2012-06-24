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

(defn neighbours
  [blog post]

  (if (= (first blog) post)
    [nil (second blog)]
    (loop [prev (first blog)
           posts (rest blog)]
      (if (= (first posts) post)
        [prev (second posts)]
        (recur (first posts) (rest posts))))))
