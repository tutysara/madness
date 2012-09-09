(ns madness.render
  "## High-level rendering

  In this part of the documentation, the various parts of the
  templates will be explained in detail: how they are built up, how
  madness recognises various parts of it, and so on and so forth. This
  is a high-level overview, but pointers will be given to places that
  go into the tinies details.

  For a full-blown example, check the [asylum][1] branch of this
  project, which has templates, posts and pages too.

  [1]: https://github.com/algernon/madness/tree/asylum"

  ^{:author "Gergely Nagy <algernon@madhouse-project.org>"
    :copyright "Copyright (C) 2012 Gergely Nagy <algernon@madhouse-project.org>"
    :license {:name "GNU General Public License - v3"
              :url "http://www.gnu.org/licenses/gpl.txt"}}

  (:require [madness.io :as io]
            [madness.blog :as blog]
            [madness.blog.index :as blog-index]
            [madness.blog.archive :as blog-archive]
            [madness.blog.post :as blog-post]
            [madness.blog.page :as blog-page]
            [madness.blog.atom :as blog-feed]
            [madness.config :as cfg]
            [madness.utils :as utils]))

;; Convenience Var containing all the blog posts
(def blog-posts (blog/load-posts))
;; Convenience Var contiaining all the blog pages
(def blog-pages (blog/load-pages))
;; Convenience Var containing all the blog posts, grouped by tags.
(def blog-tag-grouped (utils/group-blog-by-tags blog-posts))

(defn- render-to-file
  "Render a post or page to a file, using a custom render function."
  [all-posts current-post render-fn file-name]

  (io/write-out-dir file-name
                    (apply str (render-fn current-post all-posts))))

;; ### Rendering
;;
;; Every part of the site - at least the HTML parts - have a common
;; structure: a [navigation bar][1], the content area, a sidebar, and
;; a footer. These are common to all pages and posts, archives and
;; everything else too.
;;
;; What is inside the content area, varies by what page we're talking
;; about - as it will be explained just below.
;;
;; [1]: #madness.blog.nav
;;
(defmulti render
  "Render a part of the site to files."
  (fn [part & args] part))

;; ### The site index
;;
;; Renders the main entry point of the site into the content area, and
;; saves the result into a file named `index.html`.
;;
;; The content area will consist of the latest blog post, followed by
;; a limited number of recent items: the configuration controls how
;; many of these are displayed at most.
;;
;; Blog entries that are too old to fit, will only be available
;; through the archive, and they're not displayed on the index at all.
;;
;; For more information  about how the content area is  built, see the
;; [blog.index][1] namespace.
;;
;; [1]: #madness.blog.index
;;
(defmethod render :index [_]
  (render-to-file blog-posts nil blog-index/blog-index "index.html")) ;; @change - change this ordering for easy understanding

;; ### The global archive
;;
;; Renders the global archive to a file named `blog/archives/index.html`.
;;
;; The archive is not much more than a title in place of a highlighted
;; post, followed by a limited set of recent posts, then the archived posts.
;;
;; The archived posts are rendered differently than the recent posts
;; (see the [blog.archive][1] namespace!), and by default, their
;; number is not limited. Madness does not implement any kind of
;; archive paging.
;;
;; [1]: #madness.blog.archive
;;
(defmethod render :archive [_]
  (render-to-file blog-posts blog-posts
                  (partial blog-archive/blog-archive "Archive" "/blog/atom.xml")
                  "blog/archives/index.html"))

;; ### The tag archives
;;
;; Each tag has an archive of its own, in files named after the tag,
;; something like `blog/tag/index.html`.
;;
;; These pages are exactly the same as the global archive above,
;; except that in the recent and archived posts area, only those posts
;; are shown, that are tagged with the appropriate tag.
;;

;; To render all the archives for each and every tag, we need a
;; function that can render one.
(defmethod render :tag-archive
  [_ all-posts tag tagged-posts]
  
  (let [fn (str "." (utils/tag-to-url tag) "index.html")]
    (render-to-file all-posts tagged-posts
                    (partial blog-archive/blog-archive (str "Tag: " tag)
                             (str "" (utils/tag-to-url tag) "atom.xml")) fn)))

;; And another, that maps through the tags, and using the previous
;; method, renders an archive for each of them.
(defmethod render :tags [_]
  (dorun (map #(render :tag-archive blog-posts %1 (get blog-tag-grouped %1))
              (keys blog-tag-grouped))))

;; ### Blog posts
;;
;; Blog posts are pretty simple: they show the post itself, the date
;; it was posted, the tags it is tagged with, and any previous or next
;; articles, for easier navigation.
;;
;; There is no recent post or archived posts area here, but commenting
;; can be enabled on a per-post basis.
;;
;; For more information on how a post is rendered, see the
;; [blog.post][1] namespace.
;;
;; [1]: #madness.blog.post
;;

;; Similar to how it was done with the tag archives before, to render
;; all posts, we must first be able to render one. Fortunately for us,
;; blog posts have an `:url` property, which we can re-use for their
;; filename.
;;
;; By default (see [blog.post[1]), this will be of the
;; `YEAR/MONTH/DATE/title/` format, to which, we append `index.html`.
;;
;; [1]: #madness.blog.post
(defmethod render :post
  [_ all-posts post]

  (let [file-name (str "." (:url post) "index.html")]
    (render-to-file all-posts post blog-post/blog-post file-name)))

;; And now that we can render a single post, we shall render them all!
(defmethod render :posts [_]
  (dorun (map (partial render :post blog-posts) blog-posts)))

;; ### Static pages
;;
;; Static pages are very much like blog posts, except they are not
;; tagged, there is no previous or next page, and their URL is not
;; date based, but determined (see [blog.page][1]) by their place on
;; the filesystem.
;;
;; [1]: #madness.blog.page
;;

;; As always, we render a single page first.
(defmethod render :page
  [_ all-posts page]

  (let [fn (str "." (:url page))]
    (render-to-file all-posts page blog-page/blog-page fn)))

;; Then map through all of them, to render them all.
(defmethod render :pages [_]
  (dorun (map (partial render :page blog-posts) blog-pages)))

;; ### Atom feeds
;;
;; Atom feeds are a bit different than the HTML representation, the
;; feed has a well defined [spec][1]. We're going to generate two
;; kinds of feeds: a global one, including all blog posts (but only
;; blog posts, not pages), and per-tag feeds, that only include posts
;; for the particular tag.
;;
;; For more information about how a feed is assembled, see the
;; [blog.atom][2] namespace!
;;
;; [1]: http://atomspec.org/
;; [2]: #madness.blog.atom

;; The main feed will be saved into `blog/atom.xml`.
(defmethod render :main-feed [_]
  (io/write-out-dir "blog/atom.xml"
                    (blog-feed/emit-atom (cfg/atom-feed :title) "/blog/" blog-posts)))

;; A single per-tag feed is saved into something like
;; `blog/tag/atom.xml`, similarly to how the per-tag archives were
;; rendered.
(defmethod render :tag-feed
  [_ tag tagged-posts]

  (let [fn (str "." (utils/tag-to-url tag) "atom.xml")]
    (io/write-out-dir fn
                      (blog-feed/emit-atom
                       (str (cfg/atom-feed :title) ":" tag)
                       (utils/tag-to-url tag)
                       tagged-posts))))

;; And as usual, since we can render a feed for a single tag, mapping
;; through all the tags is all it takes to render them all.
(defmethod render :tag-feeds [_]
  (dorun (map #(render :tag-feed %1 (get blog-tag-grouped %1))
              (keys blog-tag-grouped))))
