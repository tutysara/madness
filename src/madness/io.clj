(ns madness.io
  "I/O helper routines"

  ^{:author "Gergely Nagy <algernon@madhouse-project.org>"
    :copyright "Copyright (C) 2012 Gergely Nagy <algernon@madhouse-project.org>"
    :license {:name "GNU General Public License - v3"
              :url "http://www.gnu.org/licenses/gpl.txt"}}

  (:require [madness.config :as cfg]
            [clojure.string :as str]
            [fs.core :as fs]))

(defn write-out-dir
  "Given a filename, write a string into it, creating the file and the
  directories if needed. The destination directory can be overridden
  via the configuration mechanism."

  [file-name str]

  (let [file-path (str/join "/" [(cfg/dirs :output) file-name])] ;; @change - changed fo file name fo keep in different from fn
    (println "Writing " file-path "...") ;; @change - try to use logs if necessary
    (fs/mkdirs (fs/parent file-path))
    (spit file-path str :encoding "UTF-8")))
