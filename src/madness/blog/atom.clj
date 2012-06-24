(ns madness.blog.atom
  (:require [hiccup.core :as hicc]
            [hiccup.util :as hicc-util]
            [net.cgrand.enlive-html :as h]
            [madness.config :as cfg]))


(h/deftemplate bare-post "templates/empty.html"
  [post]

  [:html] (h/substitute (:summary post) (:content post)))

(defn emit-atom
  [blog-posts]

  (hicc/html
   "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
   [:feed {:xmlns "http://www.w3.org/2005/Atom"}
    [:title "asdf"]
    ;[:link {:href (str (:base site) "/atom.xml"), :rel "self"}]
    ;[:link {:href (:base site)}]
    ;[:updated (conv/date->xml-schema (:date site))]
    ;[:id (:base site)]
    [:author [:name "BLAH"]]

    (for [post (take 2 blog-posts)]
      [:entry
       [:title (:title post)]
       ;[:link  (str (:base site) (:url post))]
       ;[:updated (conv/date->xml-schema (:date post))]
       ;[:id (str (:base site) (:url post))]
       [:content {:type "html"}
        (hicc-util/escape-html (apply str (bare-post post)))
        ]])]))
