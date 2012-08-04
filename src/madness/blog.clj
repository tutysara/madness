(ns madness.blog
  "Post & page loading functions."
  (:require [madness.blog.post :as blog-post]
            [madness.blog.page :as blog-page]
            [madness.config :as cfg])
  (:import (java.io File)
           (org.apache.commons.io FileUtils FilenameUtils)))

(defn list-files
  "List all HTML files within a given directory. Returns an array."
  
  [dir]

  (let [d (File. dir)]
    (if (.isDirectory d)
      (sort #(compare %2 %1)
       (FileUtils/listFiles d (into-array ["html"]) true)) [] )))

(defn load-posts
  "Load all posts for the blog. Returns a sequence of processed
  blog posts."

  []

  (map blog-post/read-post (list-files (cfg/dirs :posts))))

(defn load-pages
  "Load all pages for the blog. Returns a sequence of processed blog
  pages."
  
  []

  (map blog-page/read-page (list-files (cfg/dirs :pages))))
