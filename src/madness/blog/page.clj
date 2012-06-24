(ns madness.blog.page
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

(defn- page-url
  [path]

  (second (first (re-seq (re-pattern (str ".*" (cfg/dirs :pages) "(.*)"))
                         path))))

(defn read-page
  [file]

  (let [page (h/html-resource file)]
    {:title (apply h/text (h/select page [:article :title])),
     :url (page-url (.getPath file))
     :comments (-> (first (h/select page [:article])) :attrs :comments enabled?),
     :content (h/select page [:section])}))

(h/defsnippet blog-page-title (cfg/template) [:.hero-unit :h1]
  [title]
  [:h1] (h/content title))

(h/defsnippet blog-page-disqus (cfg/template) [:#disqus]
  [page]

  [:#disqus] (when (:comments page) identity))

(h/deftemplate blog-page (cfg/template)
  [page all-posts]

  [:title] (h/content (:title page) " - Asylum")
  [:#recents] nil
  [:#archive] nil
  [:#post-neighbours] nil
  [:.hero-unit] (h/do->
                 (h/content (blog-page-title (:title page))
                            (:content page)
                            (blog-page-disqus page)))
  [:#nav-recent-posts :ul :li] (blog-nav/recent-posts all-posts)
  [:#nav-tags :ul :li] (blog-nav/all-tags all-posts))
