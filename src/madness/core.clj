;; ## Madness
;;
;; Madness is a static site generator tool, primarily aimed at
;; generating a blog, but also supports static pages too. Among its
;; features are support for tags (and per-tag archives and per-tag
;; Atom feeds), and using one single template for each rendered
;; format: one for HTML, another for the Atom feeds, and the template
;; contain no logic at all.
;;
;; The reason behind that is that I wanted to preview the template in
;; my browser, without having to go an extra mile. Since both the blog
;; post pages and the static pages look very much alike - except for
;; one being tagged, and the other not, there was no reason to
;; separate them on the template level. The sidebar is exactly the
;; same in both cases, so is the navigation bar. These tiny
;; differences can easily be overridden from the generator code.
;;
;; We will touch on the subject of templates [later][1], for now, let us
;; concentrate on how to start the building process!
;;
;; [1]: #madness.render
;;

(ns madness.core
  "Entry point into madness, usage:

    $ lein run -m madness.core
    $ lein run -m madness.core :index :archive"

  ^{:author "Gergely Nagy <algernon@madhouse-project.org>"
    :copyright "Copyright (C) 2012 Gergely Nagy <algernon@madhouse-project.org>"
    :license {:name "GNU General Public License - v3"
              :url "http://www.gnu.org/licenses/gpl.txt"}}
  
  (:require [madness.render :as render]))

(defn- str->keyword
  "Takes a string, strips the first char (assumed to be `:`), and
  returns a keyword."

  [s]

  (keyword (apply str (rest s))))

(defn -main
  "The main entry point of Madness: when called without arguments,
  generates everything. If called with a list of (string) keywords,
  only generates the given parts of the blog.

  The following keywords are understood:

  * `:index`: The main index page of the blog.
  * `:archive`: The main archive page of the blog.
  * `:tags`: All of the per-tag archives of the blog.
  * `:posts`: All of the posts that belong to the blog.
  * `:pages`: All of the pages that accompany the blog.
  * `:main-feed`: The main Atom feed.
  * `:tag-feeds`: The per-tag Atom feeds."
  
  ([] (-main ":index" ":archive" ":tags" ":posts" ":main-feed" ":pages" ":tag-feeds"))
  ([& args] (dorun (map render/render (map str->keyword args)))))
