(ns madness.core
  "Entry point into madness, usage:

    $ lein run -m madness.core
    $ lein run -m madness.core :index :archive"

  ^{:author "Gergely Nagy <algernon@madhouse-project.org>"
    :copyright "Copyright (C) 2012 Gergely Nagy <algernon@madhouse-project.org>"
    :license {:name "GNU General Public License - v3"
              :url "http://www.gnu.org/licenses/gpl.txt"}}
  
  (:require [madness.render :as render]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.file :as ring-file]))

(defn- str->keyword
  "Takes a string, strips the first char (assumed to be `:`), and
  returns a keyword."

  [s]

  (keyword (apply str (rest s))))

(defn -main
  "The main entry point of Madness: when called without arguments,
  generates everything. If called with a list of (string) keywords,
  only generates the given parts of the blog.  The following keywords are understood:

  * `:index`: The main index page of the blog.
  * `:archive`: The main archive page of the blog.
  * `:tags`: All of the per-tag archives of the blog.
  * `:posts`: All of the posts that belong to the blog.
  * `:pages`: All of the pages that accompany the blog.
  * `:main-feed`: The main Atom feed.
  * `:tag-feeds`: The per-tag Atom feeds."
  
  ([] (-main ":index" ":archive" ":tags" ":posts" ":main-feed" ":pages" ":tag-feeds"))
  ([& args] (dorun (map render/render (map str->keyword args)))))

(defn tst-server
  "Runs the test server from the public directory"
  []
  (let [public "/home/tutysra/src/myprojects/site/tutysara.github.com"]
    (jetty/run-jetty
     (-> (fn [req])
         (ring-file/wrap-file public))
     {:port 8080 :join? false})))
