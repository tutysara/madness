(ns madness.core
  (:require [madness.io :as io]
            [madness.blog :as blog]
            [madness.blog.index :as blog-index]
            [madness.blog.post :as blog-post]))

(def blog-posts (blog/load-posts))

(defn- render-post
  [all-posts post]

  (let [fn (str "." (:url post) "index.html")]
    (io/write-out-dir fn
                      (apply str (blog-post/blog-post post all-posts)))))

(defn -main
  []

  (io/write-out-dir "index.html"
                    (apply str (blog-index/blog-index blog-posts)))

  (dorun (map (partial render-post blog-posts) blog-posts)))
