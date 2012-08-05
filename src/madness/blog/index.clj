(ns madness.blog.index

  ^{:author "Gergely Nagy <algernon@madhouse-project.org>"
    :copyright "Copyright (C) 2012 Gergely Nagy <algernon@madhouse-project.org>"
    :license {:name "GNU General Public License - v3"
              :url "http://www.gnu.org/licenses/gpl.txt"}}
  
  (:require [net.cgrand.enlive-html :as h]
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

(h/defsnippet blog-index-first-title (cfg/template) [:.hero-unit :h1]
  [post]

  [:h1] (h/set-attr :title (:title post))
  [:h1 :a] (utils/rewrite-link (:url post) (:title post)))

(h/deftemplate blog-index (cfg/template)
  [blog-posts _]

  [:.hero-unit] (h/do->
                 (h/content (blog-index-first-title (first blog-posts))
                            (:summary (first blog-posts))
                            (index-read-on (first blog-posts))))
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
