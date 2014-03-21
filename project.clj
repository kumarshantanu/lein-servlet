(defproject lein-servlet/parent "0.0.0"
  :description "Housekeeping project for lein-servlet"
  :url "https://github.com/kumarshantanu/lein-servlet"
  :plugins [[lein-sub "0.3.0"]]
  :sub ["adapter-jetty7"
        "adapter-jetty8"
        "adapter-jetty9"
        "adapter-tomcat7"
        "adapter-tomcat8"
        "lein-template"
        "plugin"]
  :eval-in :leiningen)