# Changes and TODO


## 2012-????-?? / 0.3.0

* [TODO] First class Ring support (co-existing Clojure/Other webapp)
* [TODO] JNDI DataSource configuration
* [TODO] Support servlet filters :filters
* [TODO] Support servlet listeners :listeners
* [TODO] Template for SpringMVC
* [TODO] Template for Struts2
* [TODO] Template for Jython/modjy/WSGI/flask/jinja2
* [TODO] Template for Ruby/jrack/Sinatra


## 2012-July-?? / 0.2.0

* Servlet-engine adapters:
  * Jetty 7.6.4.v20120524 `lein-servlet/adapter-jetty7` `0.2.0`
  * Jetty 8.1.4.v20120524 `lein-servlet/adapter-jetty8` `0.2.0`
  * Tomcat 7.0.29 `lein-servlet/adapter-tomcat7` `0.2.0`
* Plugin `lein-servlet` `0.2.0`
  * Support :war-exclusions and :uberwar-exclusions under :servlet
  * Rename :classes to :servlets [TODO](README)
  * Handle servlet init params in adapters and auto-generated web.xml (WAR file)
  * Generate WAR file for webapp from provided/auto-generated web.xml
* Templates: `lein-servlet/lein-template` `0.2.0`
  * Point to plugin version `0.2.0`


## 2012-June-17 / 0.1.0

* Servlet-engine adapters:
  * Jetty 7.6.3.v20120416 `lein-servlet/adapter-jetty7` `0.1.0`
  * Jetty 8.1.3.v20120416 `lein-servlet/adapter-jetty8` `0.1.0`
  * Tomcat 7.0.27 `lein-servlet/adapter-tomcat7` `0.1.0`
* Plugin `lein-servlet` `0.1.0`
  * Configuration support for
    * Servlet classes
    * Web descriptor `web.xml`
  * SSL support
  * JSP support
  * Display info on loaded-engines: `lein servlet engine`
  * Run webapp in interactive mode: `lein servlet run`
* Templates: `lein-servlet/lein-template` `0.1.0`
  * Clojure webapp based on servlet
  * Clojure/Java webapp based on Struts `1.2.9`
  * Clojure/PHP webapp based on Quercus `4.0.28`
  * Clojure/CFML webapp based on Railo `3.1.000`

