(defproject madness "0.0.0-SNAPSHOT"
  :description "Static site generator, based on Enlive."
  :url "https://github.com/algernon/madness"
  :license {:name "GNU General Public License - v3"
            :url "http://www.gnu.org/licenses/gpl.txt"
            :distribution :repo}
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [enlive/enlive "1.0.0"]
                 [clj-time "0.4.3"]
                 [org.clojars.amit/commons-io "1.4.0"]
                 [marginalia "0.7.0"]]
  :plugins [[lein-marginalia "0.7.0"]])
