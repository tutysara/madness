(ns madness.blog.post
  (:require [net.cgrand.enlive-html :as h]
            [madness.blog.nav :as blog-nav]
            [madness.utils :as utils]
            [madness.config :as cfg]
            [clojure.string :as str]
            [clj-time.format :as time-format]))

(defn- enabled?
  [value]

  (if (nil? value)
    false
    true))

(def blog-date-format ^{:private true}
  (time-format/formatter "YYYY-MM-dd HH:mm"))

(defn- post-url
  [date fn]

  (str "/blog/" (time-format/unparse (time-format/formatter "YYYY/MM/dd") date)
       "/" (second (first (re-seq #"....-..-..-(.*).html" fn))) "/"))

(defn read-post
  [file]

  (let [post (h/html-resource file)
        date (time-format/parse blog-date-format (apply h/text (h/select post [:article :date])))]
    {:title (apply h/text (h/select post [:article :title])),
     :tags (map h/text (h/select post [:article :tags :tag])),
     :summary (h/select post [:summary :> h/any-node]),
     :date date,
     :url (post-url date (.getName file))
     :comments (-> (first (h/select post [:article])) :attrs :comments enabled?),
     :content (h/select post [:section])}))

(h/defsnippet blog-post-title (cfg/template) [:.hero-unit :h1]
  [title]
  [:h1] (h/content title))

(h/defsnippet blog-post-tag (cfg/template) [:#full-article-footer :a]
  [tag]

  [:a] (h/set-attr :href (utils/tag-to-url tag))
  [:a] (h/do->
        (h/content tag)
        (h/after " ")))

(h/defsnippet blog-post-footer (cfg/template) [:#full-article-footer]
  [post]

  [:a] (h/clone-for [tag (:tags post)]
                    (h/substitute (blog-post-tag tag)))
  [:#post-date] (h/do->
                 (h/content (utils/date-format (:date post)))
                 (h/remove-attr :id))
  [:#full-article-footer] (h/remove-attr :id))

(h/deftemplate blog-post (cfg/template)
  [post all-posts]

  [:title] (h/content (:title post) " - Asylum")
  [:#recents] nil
  [:.hero-unit] (h/do->
                 (h/content (blog-post-title (:title post))
                            (:summary post)
                            (:content post)
                            (blog-post-footer post)))

  [:#nav-recent-posts :ul :li] (blog-nav/recent-posts all-posts)
  [:#nav-tags :ul :li] (blog-nav/all-tags all-posts))
