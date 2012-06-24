(ns madness.core
  (:require [madness.io :as io]
            [madness.blog :as blog]
            [madness.blog.index :as blog-index]
            [madness.blog.archive :as blog-archive]
            [madness.blog.post :as blog-post]
            [madness.utils :as utils]))

(def blog-posts (blog/load-posts))

(defn- render-post
  [all-posts post]

  (let [fn (str "." (:url post) "index.html")]
    (io/write-out-dir fn
                      (apply str (blog-post/blog-post post all-posts)))))

(defn- render-archive
  [all-posts tag tagged-posts]

  (let [fn (str "." (utils/tag-to-url tag) "index.html")]
    (io/write-out-dir fn
                      (apply str (blog-archive/blog-archive all-posts tagged-posts)))))

(defn -main
  []

  (io/write-out-dir "index.html"
                    (apply str (blog-index/blog-index blog-posts)))

  (io/write-out-dir "archive.html"
                    (apply str (blog-archive/blog-archive blog-posts blog-posts)))

  (let [tag-grouped (utils/group-blog-by-tags blog-posts)]
    (dorun (map #(render-archive blog-posts %1 (get tag-grouped %1))
                (keys tag-grouped))))
  
  (dorun (map (partial render-post blog-posts) blog-posts)))
