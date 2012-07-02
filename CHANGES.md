# Changes and TODO


## 2012-July-?? / 0.2.0

* [TODO] JNDI DataSource configuration
* [TODO] Place web.xml outside of 'public' in templates
* Generate WAR file for webapps - TODO: Autogenerate web.xml for servlets-only


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

