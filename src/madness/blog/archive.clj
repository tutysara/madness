(ns madness.blog.archive
  (:require [net.cgrand.enlive-html :as h]
            [madness.blog.post :as blog-post]
            [madness.blog :as blog]
            [madness.blog.nav :as blog-nav]
            [madness.utils :as utils]
            [madness.config :as cfg]
            [madness.blog.recent :as blog-recent]
            [clojure.string :as str]))

(h/defsnippet archive-post-item (cfg/template) [:#archive-post]
  [post]

  [:#archive-post :a] (h/set-attr :href (:url post))
  [:#archive-post-title] (h/do->
                          (h/remove-attr :id)
                          (h/content (:title post)))
  [:#archive-post-date :span] (h/substitute (utils/date-format (:date post)))
  [:#archive-post-date] (h/remove-attr :id)
  [:#archive-post] (h/remove-attr :id))

(h/defsnippet archive-posts (cfg/template) [:#archive]
  [posts]

  [:#archive] (h/remove-attr :id)
  [:#archive-post] (h/clone-for [p posts]
                                (h/do->
                                 (h/substitute (archive-post-item p))
                                 (h/after [{:tag :hr}]))))

(h/deftemplate blog-archive (cfg/template)
  [title all-posts blog-posts]

  [:.hero-unit :h1] (h/content title)
  [:.hero-unit :p] nil
  [:#hero-full] nil
  [:#full-article-footer] nil
  [:#post-neighbours] nil
  [:#disqus] nil
  [:#archive] (h/substitute
               (archive-posts (drop (dec (cfg/recent-posts :total)) blog-posts)))
  [:#recents]
    (h/clone-for [rows (utils/blog->table
                        (cfg/recent-posts :columns)
                        (cfg/recent-posts :rows) blog-posts)]
                 (h/do->
                  (h/substitute (blog-recent/recent-post-row rows true))
                  (h/before [{:tag :hr}])))
  [:#nav-recent-posts :ul :li] (blog-nav/recent-posts all-posts)
  [:#nav-tags :ul :li] (blog-nav/all-tags all-posts))
