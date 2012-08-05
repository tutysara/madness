(ns madness.blog.post

  ^{:author "Gergely Nagy <algernon@madhouse-project.org>"
    :copyright "Copyright (C) 2012 Gergely Nagy <algernon@madhouse-project.org>"
    :license {:name "GNU General Public License - v3"
              :url "http://www.gnu.org/licenses/gpl.txt"}}
  
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
  (time-format/formatter "yyyy-MM-dd HH:mm"))

(defn- post-url
  [date fn]

  (str "/blog/" (time-format/unparse (time-format/formatter "yyyy/MM/dd") date)
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
  [:h1] (h/do->
         (h/content title)
         (h/set-attr :title title)))

(h/defsnippet blog-post-tag (cfg/template) [:#full-article-footer :a]
  [tag]

  [:a] (h/do->
        (h/set-attr :href (utils/tag-to-url tag))
        (h/after " "))
  [:a :span] (h/substitute tag))

(h/defsnippet blog-post-footer (cfg/template) [:#full-article-footer]
  [post]

  [:a] (h/clone-for [tag (:tags post)]
                    (h/substitute (blog-post-tag tag)))
  [:#post-date] (h/do->
                 (h/content (utils/date-format (:date post)))
                 (h/remove-attr :id))
  [:#full-article-footer] (h/remove-attr :id))

(h/defsnippet blog-post-neighbours (cfg/template) [:#post-neighbours]
  [neighbours]

  [:.pull-left :a :span] (h/substitute (:title (first neighbours)))
  [:.pull-left :a] (h/set-attr :href (:url (first neighbours)))
  [:.pull-left] (if (empty? (first neighbours))
                  nil
                  identity)
  
  [:.pull-right :a :span] (h/substitute (:title (last neighbours)))
  [:.pull-right :a] (h/set-attr :href (:url (last neighbours)))
  [:.pull-right] (if (empty? (last neighbours))
                   nil
                   identity)
  [:#post-neighbours] (h/remove-attr :id))

(h/defsnippet blog-post-disqus (cfg/template) [:#disqus]
  [post]

  [:#disqus] (when (:comments post) identity))

(h/deftemplate blog-post (cfg/template)
  [post all-posts]

  [:title] (h/content (:title post) " - Asylum")
  [:#recents] nil
  [:#archive] nil
  [:.hero-unit] (h/do->
                 (h/content (blog-post-title (:title post))
                            (:summary post)
                            (:content post)
                            (blog-post-footer post)
                            (blog-post-disqus post)))
  [:#post-neighbours] (h/substitute (blog-post-neighbours (utils/neighbours all-posts post)))
  [:#nav-recent-posts :ul :li] (blog-nav/recent-posts all-posts)
  [:#nav-tags :ul :li] (blog-nav/all-tags all-posts))
