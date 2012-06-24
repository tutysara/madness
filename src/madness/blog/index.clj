(ns madness.blog.index
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

  [:.hero-unit] (h/content (blog-post/blog-post-title (:title (first blog-posts)))
                           (:summary (first blog-posts))
                           (index-read-on (first blog-posts)))
  [:#recents]
    (h/clone-for [rows (utils/blog->table
                        (cfg/recent-posts :columns)
                        (cfg/recent-posts :rows) (rest blog-posts))]
                 (h/do->
                  (h/substitute (recent-post-row rows))
                  (h/before utils/hr-desktop)))
  [:#post-neighbours] nil
  [:#archive] nil  
  [:#nav-recent-posts :ul :li] (blog-nav/recent-posts blog-posts)
  [:#nav-tags :ul :li] (blog-nav/all-tags blog-posts))
