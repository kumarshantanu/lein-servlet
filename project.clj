(defproject lein-servlet/parent "0.0.0"
  :description "Housekeeping project for lein-servlet"
  :plugins [[lein-sub "0.2.0"]]
  :sub ["adapter-jetty7"
        "adapter-jetty8"
        "adapter-tomcat7"
        "lein-template"
        "plugin"])
