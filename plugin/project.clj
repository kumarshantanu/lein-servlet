(defproject lein-servlet "0.1.0"
  :description "Leiningen plugin to work with servlet based webapps"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.0.0"
  :dependencies []
  :warn-on-reflection true
  :profiles {:dev {:dependencies [[leiningen-core "2.0.0-preview6"]]}}
  :aliases  {"dev" ["with-profile" "dev"]}
  :eval-in-leiningen true)
