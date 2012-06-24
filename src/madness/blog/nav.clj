(ns madness.blog.nav
  (:require [net.cgrand.enlive-html :as h]
            [madness.config :as cfg]
            [madness.utils :as utils]))

(h/defsnippet recent-item "templates/asylum3-main.html" [:#nav-recent-posts :ul :li]
  [title url]

  [:a] (h/do->
        (h/set-attr :href url)
        (h/content title)))

(h/defsnippet tag-item "templates/asylum3-main.html" [:#nav-tags :ul :li]
  [tag]

  [:a] (h/do->
        (h/set-attr :href (utils/tag-to-url tag))
        (h/content tag)))

(defn recent-posts
  [all-posts]

  (h/clone-for [post (take (cfg/recent-posts :total) all-posts)]
               (h/substitute (recent-item (:title post) (:url post)))))

(defn all-tags
  [all-posts]

  (h/clone-for [tag (utils/tags all-posts)]
               (h/substitute (tag-item tag))))
