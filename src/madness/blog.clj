(ns madness.blog
  "## Load blog posts & pages

  To build a whole site, madness will first load all the posts and
  pages, and turn them into data structures that are easy to work
  with. This is far from efficient, but even for a moderately sized
  site, neither speed nor memory requirements are particularly bad."

  ^{:author "Gergely Nagy <algernon@madhouse-project.org>"
    :copyright "Copyright (C) 2012 Gergely Nagy <algernon@madhouse-project.org>"
    :license {:name "GNU General Public License - v3"
              :url "http://www.gnu.org/licenses/gpl.txt"}}

  (:require [madness.blog.post :as blog-post]
            [madness.blog.page :as blog-page]
            [madness.config :as cfg]
            [fs.core :as fs]))

(defn list-files
  "List all HTML files within a given directory. Returns an array."
  
  [dir]

  (sort #(compare %2 %1) (fs/find-files dir #".*\.html$")))

(defn load-posts
  "Load all posts for the blog. Returns a sequence of processed blog
  posts. See the [blog.post][1] namespace for more information about
  how a processed post looks like.

  [1]: #madness.blog.post"

  []

  (map blog-post/read-post (list-files (cfg/dirs :posts))))

(defn load-pages
  "Load all pages for the blog. Returns a sequence of processed blog
  pages. See the [blog.page][1] namespace for more information about
  how a processed page looks like.

  [1]: #madness.blog.page"
  
  []

  (map blog-page/read-page (list-files (cfg/dirs :pages))))
