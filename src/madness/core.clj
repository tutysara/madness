(ns madness.core
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

(defn- render-post
  [all-posts post]

  (let [fn (str "." (:url post) "index.html")]
    (render-to-file all-posts post blog-post/blog-post fn)))

(defn- render-page
  [all-posts page]

  (let [fn (str "." (:url page))]
    (render-to-file all-posts page blog-page/blog-page fn)))

(defn- render-archive
  [all-posts tag tagged-posts]

  (let [fn (str "." (utils/tag-to-url tag) "index.html")]
    (render-to-file all-posts tagged-posts
                    (partial blog-archive/blog-archive (str "Tag: " tag)) fn)))

(defn- render-feed
  [tag tagged-posts]

  (let [fn (str "." (utils/tag-to-url tag) "atom.xml")]
    (io/write-out-dir fn
                      (blog-feed/emit-atom
                       (str (cfg/atom-feed :title) ":" tag)
                       (utils/tag-to-url tag)
                       tagged-posts))))

(defn -main
  ([] (-main ":index" ":archive" ":tags" ":posts" ":main-feed" ":pages" ":tag-feeds"))
  ([& args]
     
     (when (some #(= ":index" %1) args)
       (render-to-file nil blog-posts blog-index/blog-index "index.html"))

     (when (some #(= ":archive" %1) args)
       (render-to-file blog-posts blog-posts
                       (partial blog-archive/blog-archive "Archive")
                       "blog/archives/index.html"))

     (when (some #(= ":tags" %1) args)
       (dorun (map #(render-archive blog-posts %1 (get blog-tag-grouped %1))
                   (keys blog-tag-grouped))))

     (when (some #(= ":posts" %1) args)
       (dorun (map (partial render-post blog-posts)
                   blog-posts)))

     (when (some #(= ":pages" %1) args)
       (dorun (map (partial render-page blog-posts)
                   blog-pages)))

     (when (some #(= ":main-feed" %1) args)
       (io/write-out-dir "blog/atom.xml"
                         (blog-feed/emit-atom (cfg/atom-feed :title) "/blog/" blog-posts)))

     (when (some #(= ":tag-feeds" %1) args)
       (dorun (map #(render-feed %1 (get blog-tag-grouped %1))
                   (keys blog-tag-grouped))))))
