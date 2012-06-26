(ns madness.blog.recent
  (:require [net.cgrand.enlive-html :as h]
            [madness.utils :as utils]
            [madness.config :as cfg]))

(h/defsnippet recent-post-tag (cfg/template) [:#recent-posts :.tag]
  [tag]

  [:a] (h/do->
        (h/remove-class "tag")
        (h/set-attr :href (utils/tag-to-url tag))
        (h/after " "))
  [:a :span] (h/substitute tag))

(h/defsnippet recent-post-item (cfg/template) [:.recent-post]
  [post]

  [:.recent-post] (h/remove-class "recent-post")
  [:h2 :a] (h/do->
            (h/content " " (:title post))
            (h/set-attr :href (:url post)))
  [:.post-date] (h/substitute (utils/date-format (:date post)))
  [:.tag] (h/clone-for [tag (:tags post)]
                       (h/substitute (recent-post-tag tag)))
  [:p.summary] (h/do->
                (h/remove-attr :class)
                (h/substitute (:summary post))))

(h/defsnippet recent-post-row (cfg/template) [:#recents]
  [posts archive?]

  [:#recents] (h/remove-attr :id)
  [:#recent-posts :.recent-post]
    (h/clone-for [p posts]
                 (h/do->
                  (h/substitute (recent-post-item p))
                  (h/set-attr :class (str "span" (cfg/recent-posts :span)))))
  [:#recent-posts] (h/do->
                    (h/remove-attr :id)
                    (if archive?
                      (h/remove-class "visible-desktop")
                      identity)))
