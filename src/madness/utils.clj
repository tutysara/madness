(ns madness.utils
  (:require [clojure.string :as str]
            [clj-time.format :as time-format]
            [net.cgrand.enlive-html :as h]))

(def hr-desktop [{:tag :hr :attrs {:class "visible-desktop"}}])

(defn tags
  [blog]

  (apply sorted-set (mapcat :tags blog)))

(defn tag-to-url [tag]
  (str "/blog/tags/" (str/replace (str/lower-case tag) " " "-") "/"))

(defn date-format
  [date]

  (time-format/unparse (time-format/formatter "yyyy-MM-dd") date))

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

(defn blog->table
  [columns rows blog-posts]
  (loop [posts blog-posts
         c 0
         result []]
    (if (or (empty? posts) (and (> rows 0) (>= c rows)))
      result
      (recur (drop columns posts)
             (inc c)
             (conj result (take columns posts))))))

(defn rewrite-link
  [url content]

  (h/do->
   (h/set-attr :href url)
   (h/content content)))

(defn rewrite-link-with-title
  [url title]

  (h/do->
   (h/set-attr :href url)
   (h/set-attr :title title)
   (h/content " " title)))
