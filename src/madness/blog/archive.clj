(ns madness.blog.archive
  "## Low-level archive rendering

  Archives are pages that do not have any content, but the title, an
  archive-specific Atom feed, and a list of recent posts, and
  optionally archived posts."

  ^{:author "Gergely Nagy <algernon@madhouse-project.org>"
    :copyright "Copyright (C) 2012-2013 Gergely Nagy <algernon@madhouse-project.org>"
    :license {:name "Creative Commons Attribution-ShareAlike 3.0"
              :url "http://creativecommons.org/licenses/by-sa/3.0/"}}

  (:require [net.cgrand.enlive-html :as h]
            [madness.blog.post :as blog-post]
            [madness.blog :as blog]
            [madness.blog.nav :as blog-nav]
            [madness.utils :as utils]
            [madness.config :as cfg]
            [madness.blog.recent :as blog-recent]
            [clojure.string :as str]))

;; This snippet renders a single item for the archived posts table. It
;; takes the post as argument, and rewrites the
;; `#madness-archive-recent-post` item from the template, updating its
;; link and title, and also removing any other content part that
;; appears on a recent post.
(h/defsnippet archive-post-item (cfg/template) [:#madness-archive-recent-post]
  [post]

  [:#madness-archive-recent-post] (h/remove-attr :id)
  [:h3 :a] (utils/rewrite-link-with-title
             (:url post) (:title post))
  [:#madness-recent-article-meta] nil
  [:#madness-recent-article-summary] nil)

;; Renders a single row of archived posts, using the
;; `#madness-archive-recent-post-row` element of the template as
;; source.
(h/defsnippet archive-post-row (cfg/template) [:#madness-archive-recent-post-row]
  [posts]

  [:#madness-archive-recent-post-row] (h/remove-attr :id)
  [:#madness-archive-recent-post]
    (h/clone-for [p posts]
                 (h/do->
                  (h/substitute (archive-post-item p))
                  (h/set-attr :class (str "span" (cfg/archive-posts :span)))
                  (h/remove-attr :id)))
  [:#recent-posts] (h/do->
                    (h/remove-attr :id)
                    (h/remove-class "visible-desktop")))

;; Renders the whole archive page, be that the main one, or the
;; per-tag archives. The page will include the title, a list of recent
;; posts, followed by archived ones, and of course any other wrapping
;; the template may hold. This also updates the Atom feed in the page
;; (the `#main-rss` and `#rss-feed` elements) with the supplied
;; URL. Everything else is disabled.
;;
;; Uses the `#madness-archive-recent-posts` and
;; `#madness-archive-archived-posts` elements of the template mostly.
(h/deftemplate blog-archive (cfg/template)
  [title feed-url blog-posts all-posts]

  [:#madness-article :h2] (h/do->
                           (h/content title)
                           (h/set-attr :title title))
  [:.madness-article-meta] nil
  [:#madness-article-content] nil
  [:#madness-article-read-more] nil
  [:#madness-article-comments] nil
  [:#madness-article-neighbours] nil
  [:#rss-feed] (h/do->
                (h/set-attr :href feed-url)
                (h/remove-attr :id))
  [:#main-rss] (h/do->
                (h/remove-attr :id)
                (h/set-attr :href feed-url))

  ; Navigation bar
  [:#madness-recent-posts :li] (blog-nav/recent-posts all-posts)
  [:#madness-recent-posts] (h/remove-attr :id)
  [:#madness-tags :li] (blog-nav/all-tags all-posts)
  [:#madness-tags] (h/remove-attr :id)

  ; Recents & archived posts
  [:#madness-archive-recent-posts] (h/do->
                                    (h/remove-attr :id)
                                    (h/remove-class "visible-desktop"))
  [:#madness-archive-recent-post-row]
    (h/clone-for [rows (utils/blog->table
                        (cfg/recent-posts :columns)
                        (cfg/recent-posts :rows) blog-posts)]
                 (h/do->
                  (h/substitute (blog-recent/recent-post-row rows true))
                  (h/before utils/hr-desktop)))

  [:#madness-archive-archived-posts] (h/remove-attr :id)
  [:#madness-archive-archived-post-row]
     (h/clone-for [rows (utils/blog->table
                         (cfg/archive-posts :columns)
                         (cfg/archive-posts :rows)
                         (drop (* (cfg/recent-posts :columns)
                                  (cfg/recent-posts :rows)) blog-posts))]
                  (h/do->
                   (h/substitute (archive-post-row rows))))
  ; Cleanup
  [:#madness-content-area] (h/remove-attr :id)
  [:#madness-article] (h/remove-attr :id))
