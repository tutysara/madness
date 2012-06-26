(ns madness.blog.index
  (:require [net.cgrand.enlive-html :as h]
            [madness.blog.post :as blog-post]
            [madness.blog :as blog]
            [madness.blog.nav :as blog-nav]
            [madness.utils :as utils]
            [madness.config :as cfg]
            [madness.blog.recent :as blog-recent]
            [clojure.string :as str]))

(h/defsnippet index-read-on (cfg/template) [:#hero-full]
  [post]

  [:#hero-full :a] (h/set-attr :href (:url post))
  [:#hero-full] (h/remove-attr :id))

(h/defsnippet index-post-date (cfg/template) [:#hero-date]
  [post]

  [:#hero-date] (h/do->
                 (h/content (utils/date-format (:date post)))
                 (h/remove-attr :id)))

(h/deftemplate blog-index (cfg/template)
  [blog-posts]

  [:.hero-unit] (h/do->
                 (h/content (blog-post/blog-post-title (:title (first blog-posts)))
                            (:summary (first blog-posts))
                            (index-read-on (first blog-posts)))
                 (h/set-attr :title (:title (first blog-posts))))
  [:#recents]
    (h/clone-for [rows (utils/blog->table
                        (cfg/recent-posts :columns)
                        (cfg/recent-posts :rows) (rest blog-posts))]
                 (h/do->
                  (h/substitute (blog-recent/recent-post-row rows false))
                  (h/before utils/hr-desktop)))
  [:#post-neighbours] nil
  [:#archive] nil
  [:#disqus] nil
  [:#nav-recent-posts :ul :li] (blog-nav/recent-posts blog-posts)
  [:#nav-tags :ul :li] (blog-nav/all-tags blog-posts))
