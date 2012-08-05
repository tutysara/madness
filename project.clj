(defproject madness "0.0.0-SNAPSHOT"
  :description "Static site generator, based on Enlive and Bootstrap."
  :url "https://github.com/algernon/madness"
  :license {:name "GNU General Public License - v3"
            :url "http://www.gnu.org/licenses/gpl.txt"
            :distribution :repo}
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [enlive/enlive "1.0.0"]
                 [clj-time "0.4.3"]
                 [org.clojars.amit/commons-io "1.4.0"]]
  :profiles {:dev {:dependencies [[marginalia "0.7.0"]]}}
  :aliases {"build-docs" ["with-profile" "dev" "run" "-m" "madness.docs/generate-docs"]})
