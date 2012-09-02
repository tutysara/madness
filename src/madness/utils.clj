(ns madness.utils
  "Assorted utilities."

  ^{:author "Gergely Nagy <algernon@madhouse-project.org>"
    :copyright "Copyright (C) 2012 Gergely Nagy <algernon@madhouse-project.org>"
    :license {:name "GNU General Public License - v3"
              :url "http://www.gnu.org/licenses/gpl.txt"}}

  (:require [clojure.string :as str]
            [clj-time.format :as time-format]
            [net.cgrand.enlive-html :as h]))

;; A &lt;hr> element that is only visible on desktop resolutions.
(def hr-desktop [{:tag :hr :attrs {:class "visible-desktop"}}])

(defn tags
  "Return the list of unique tags, sorted alphabetically."

  [blog]

  (apply sorted-set (mapcat :tags blog)))

(defn tag-to-url
  "Given a tag, return a relative URL that points to it."

  [tag]

  (str "/blog/tags/" (str/replace (str/lower-case tag) " " "-") "/"))

(defn date-format
  "Format a date object into human-readable form."

  [date]

  (time-format/unparse (time-format/formatter "yyyy-MM-dd") date))

(defn post-tagged?
  "Determine whether a post is tagged with a given tag."

  [post tag]

  (some #(= tag %1) (:tags post)))

(defn posts-tagged
  "Return a list of posts tagged with a given tag."

  [blog-posts tag]

  (filter #(post-tagged? %1 tag) blog-posts))

(defn group-blog-by-tags
  "Group all blog posts by their tags. Posts may appear under multiple
  tags. Returns a hash-map, with the tags as keys, and the list of
  posts as values."

  [blog-posts]

  (reduce #(assoc %1 %2 (posts-tagged blog-posts %2)) {} (tags blog-posts)))

(defn neighbours
  "Given a full blog, and a single post, find the previous and the
  next post that surround the current one."

  [blog post]

  (if (= (first blog) post)
    [nil (second blog)]
    (loop [prev (first blog)
           posts (rest blog)]
      (if (= (first posts) post)
        [prev (second posts)]
        (recur (first posts) (rest posts))))))

(defn blog->table
  "Given a number of columns, rows and a list of blog-posts, arrange
  them into a table with the given number of rows and columns. If rows
  is set to zero, there will be no limit on how many rows the
  resulting table may have.

  Returns an array where each element is a list of posts for that
  row."

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
  "Rewrite an anchor element's href and content."
  [url content]

  (h/do->
   (h/set-attr :href url)
   (h/content content)))

(defn rewrite-link-with-title
  "Rewrite an anchor element's href, content and title. The content is
  the same as the title, with a single space prepended."

  [url title]

  (h/do->
   (h/set-attr :href url)
   (h/set-attr :title title)
   (h/content " " title)))
