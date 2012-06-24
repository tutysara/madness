(ns madness.io
  (:require [madness.config :as cfg]
            [clojure.string :as str])
  (:import (java.io File)
           (org.apache.commons.io FileUtils FilenameUtils)))

(defn write-out-dir
  [file str]

  (let [fn (str/join "/" ["public" file])]
    (println "Writing " fn "...")
    (FileUtils/writeStringToFile
     (File. fn) str "UTF-8")))
