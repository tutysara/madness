(ns madness.io
  "I/O helper routines"
  (:require [madness.config :as cfg]
            [clojure.string :as str])
  (:import (java.io File)
           (org.apache.commons.io FileUtils FilenameUtils)))

(defn write-out-dir
  "Given a filename, write a string into it, creating the file and the
  directories if needed. The destination directory can be overridden
  via the configuration mechanism."

  [file str]

  (let [fn (str/join "/" [(cfg/dirs :output) file])]
    (println "Writing " fn "...")
    (FileUtils/writeStringToFile
     (File. fn) str "UTF-8")))
