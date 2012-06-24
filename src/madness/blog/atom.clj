(ns madness.blog.atom
  (:require [clj-time.format :as time-format]
            [clj-time.local :as time-local]
            [net.cgrand.enlive-html :as h]
            [madness.config :as cfg]))

(def atom-date-formatter (time-format/formatter "yyyy-MM-dd'T'HH:mm:ssZZ"))

(h/deftemplate bare-post "templates/empty.html"
  [post]

  [:html] (h/substitute (:summary post) (:content post)))

(h/defsnippet atom-post (cfg/template :atom) [:entry]
  [site-base post]

  [:title] (h/content (:title post))
  [:link] (h/set-attr :href (str site-base (:url post)))
  [:updated] (h/content (time-format/unparse
                         atom-date-formatter (time-local/to-local-date-time (:date post))))
  [:id] (h/content (str site-base (:url post)))
  [:content] (h/content (apply str (bare-post post))))

(h/deftemplate atom-feed (cfg/template :atom)
  [title uri site-base posts]

  [:feed :title] (h/content title)
  [:feed] (h/set-attr :xmlns "http://www.w3.org/2005/Atom")
  [:#self] (h/do->
            (h/remove-attr :id)
            (h/set-attr :href (str site-base uri "atom.xml")))
  [:#base] (h/do->
            (h/remove-attr :id)
            (h/set-attr :href (str site-base uri)))
  [:feed :id]
    (h/content (str site-base uri))
  [:updated] (h/content (time-format/unparse atom-date-formatter (time-local/local-now)))
  [:entry] (h/clone-for [p posts]
                        (h/substitute (atom-post site-base p))))

(defn emit-atom
  [title uri posts]

  (apply str (atom-feed title uri (cfg/atom-feed :base-url) posts)))
