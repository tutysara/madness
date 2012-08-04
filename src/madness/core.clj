(ns madness.core
  (:require [madness.render :as render]))

(defn- str->keyword
  [s]

  (keyword (apply str (rest s))))

(defn -main
  ([] (-main ":index" ":archive" ":tags" ":posts" ":main-feed" ":pages" ":tag-feeds"))
  ([& args] (dorun (map render/render (map str->keyword args)))))
