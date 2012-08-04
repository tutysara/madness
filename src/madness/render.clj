(ns madness.render
  (:require [madness.io :as io]
            [madness.blog :as blog]
            [madness.blog.index :as blog-index]
            [madness.blog.archive :as blog-archive]
            [madness.blog.post :as blog-post]
            [madness.blog.page :as blog-page]
            [madness.blog.atom :as blog-feed]
            [madness.config :as cfg]
            [madness.utils :as utils]))

(def blog-posts (blog/load-posts))
(def blog-pages (blog/load-pages))
(def blog-tag-grouped (utils/group-blog-by-tags blog-posts))

(defn- render-to-file
  [all-posts current-post render-fn file]

  (io/write-out-dir file
                    (apply str (render-fn current-post all-posts))))

(defmulti render
  (fn [part & args] part))

(defmethod render :index [_]
  (render-to-file nil blog-posts blog-index/blog-index "index.html"))

(defmethod render :archive [_]
  (render-to-file blog-posts blog-posts
                  (partial blog-archive/blog-archive "Archive")
                  "blog/archives/index.html"))

(defmethod render :tag-archive
  [_ all-posts tag tagged-posts]
  
  (let [fn (str "." (utils/tag-to-url tag) "index.html")]
    (render-to-file all-posts tagged-posts
                    (partial blog-archive/blog-archive (str "Tag: " tag)) fn)))

(defmethod render :tags [_]
  (dorun (map #(render :tag-archive blog-posts %1 (get blog-tag-grouped %1))
              (keys blog-tag-grouped))))

(defmethod render :post
  [_ all-posts post]

  (let [fn (str "." (:url post) "index.html")]
    (render-to-file all-posts post blog-post/blog-post fn)))

(defmethod render :posts [_]
  (dorun (map (partial render :post blog-posts) blog-posts)))

(defmethod render :page
  [_ all-posts page]

  (let [fn (str "." (:url page))]
    (render-to-file all-posts page blog-page/blog-page fn)))

(defmethod render :pages [_]
  (dorun (map (partial render :page blog-posts) blog-pages)))

(defmethod render :main-feed [_]
  (io/write-out-dir "blog/atom.xml"
                    (blog-feed/emit-atom (cfg/atom-feed :title) "/blog/" blog-posts)))

(defmethod render :tag-feed
  [_ tag tagged-posts]

  (let [fn (str "." (utils/tag-to-url tag) "atom.xml")]
    (io/write-out-dir fn
                      (blog-feed/emit-atom
                       (str (cfg/atom-feed :title) ":" tag)
                       (utils/tag-to-url tag)
                       tagged-posts))))

(defmethod render :tag-feeds [_]
  (dorun (map #(render :tag-feed %1 (get blog-tag-grouped %1))
              (keys blog-tag-grouped))))