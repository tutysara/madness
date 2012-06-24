(ns madness.blog
  (:require [madness.blog.post :as blog-post]
            [madness.config :as cfg])
  (:import (java.io File)
           (org.apache.commons.io FileUtils FilenameUtils)))

(defn- list-files [dir]
  (let [d (File. dir)]
    (if (.isDirectory d)
      (sort #(compare %2 %1)
       (FileUtils/listFiles d (into-array ["html"]) true)) [] )))

(defn load-posts
  []

  (map blog-post/read-post (list-files (cfg/dirs :posts))))
