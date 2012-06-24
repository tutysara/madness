(ns madness.blog.archive
  (:require [net.cgrand.enlive-html :as h]
            [madness.blog.post :as blog-post]
            [madness.blog :as blog]
            [madness.blog.nav :as blog-nav]
            [madness.utils :as utils]
            [madness.config :as cfg]
            [clojure.string :as str]))

(h/defsnippet recent-post-tag (cfg/template) [:#recent-posts-footer :.tag]
  [tag]

  [:a] (h/do->
        (h/remove-class "tag")
        (h/set-attr :href (utils/tag-to-url tag))
        (h/after " "))
  [:a :span] (h/substitute tag))

(h/defsnippet recent-post-item (cfg/template) [:.recent-post]
  [post]

  [:.recent-post] (h/remove-class "recent-post")
  [:h2] (h/content (:title post))
  [:p.summary] (h/do->
                (h/remove-attr :class)
                (h/substitute (:summary post))))

(h/defsnippet recent-post-tags (cfg/template) [:#recent-posts-footer :.tag]
  [post]

  [:.tag :span] (h/clone-for [tag (:tags post)]
                             (h/do->
                              (h/content tag)
                              (h/after " "))))

(h/defsnippet recent-post-footer (cfg/template) [:#recent-posts-footer :div]
  [post]

  [:p :a.btn] (h/set-attr :href (:url post))
  [:.tag] (h/clone-for [tag (:tags post)]
                       (h/substitute (recent-post-tag tag)))
  [:p :span :span] (h/substitute (utils/date-format (:date post))))

(h/defsnippet recent-post-row (cfg/template) [:#recents]
  [posts]

  [:#recents] (h/remove-attr :id)
  [:#recent-posts :.recent-post]
    (h/clone-for [p posts]
                 (h/do->
                  (h/substitute (recent-post-item p))
                  (h/set-attr :class (str "span" (cfg/recent-posts :span)))))
  [:#recent-posts-footer :.recent-post-footer]
    (h/clone-for [p posts]
                 (h/do->
                  (h/substitute (recent-post-footer p))
                  (h/set-attr :class (str "span"
                                          (cfg/recent-posts :span)))))
  [:#recent-posts] (h/remove-attr :id)
  [:#recent-posts-footer] (h/remove-attr :id))

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
  [:#archive] (h/substitute
               (archive-posts (drop (dec (cfg/recent-posts :total)) blog-posts)))
  [:#recents]
    (h/clone-for [rows (utils/blog->table
                        (cfg/recent-posts :columns)
                        (cfg/recent-posts :rows) blog-posts)]
                 (h/do->
                  (h/substitute (recent-post-row rows))
                  (h/before [{:tag :hr}])))
  [:#nav-recent-posts :ul :li] (blog-nav/recent-posts all-posts)
  [:#nav-tags :ul :li] (blog-nav/all-tags all-posts))
