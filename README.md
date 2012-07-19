# lein-servlet

A Leiningen 2 plugin to work with servlet-based webapps.

You may use this plugin to launch a servlet-based webapp using a suitable
servlet engine adapter, or generate a war/uberwar file. Adapters for
Jetty-7, Jetty-8 and Tomcat-7 are provided.

**NOTE:** This plugin is only to work with servlets based web apps. For
idiomatic web development using Clojure you should consider
[lein-ring](https://github.com/weavejester/lein-ring)
and [ring](https://github.com/ring-clojure/ring).


## Usage

It takes multi-line configuration in `project.clj` to use `lein-servlet`. For
example, a minimal configuration might look like this:

```clojure
:plugins [[lein-servlet "0.2.0"]]
:servlet {:deps    [[lein-servlet/adapter-jetty7 "0.2.0"]]
          :webapps {"/" {:servlets {"/*" com.myapp.WebServlet}
                         :public   "public"}}}
```

Given the servlet class exists in classpath and directory `public` exists too,
when you run `lein servlet run` it starts the Jetty servlet container with the
webapp at http://localhost:3000/


### Creating a project via templates

In many cases you may be using `lein-servlet` templates that are actually
project skeletons of several types.

You do not need to have any plugin installed to create a template. Unless you
already have a version of `lein-servlet/lein-template`, the following commands
automatically download the latest version for use.

The command to create `lein-servlet` project skeletons is:

```bash
lein new lein-servlet [flavor] project-name
```

Both `flavor` and `project-name` are placeholders for the actual names to be
used. Note that `flavor` is optional and indicates a project type. Currently
the `flavor` can be either of `quercus`, `railo` and `struts`. See examples:

```bash
$ lein new lein-servlet foo          # creates a simple Clojure/servlet webapp
$ lein new lein-servlet quercus foo  # " a Clojure/PHP webapp that uses Quercus
$ lein new lein-servlet railo foo    # " a Clojure/CFML webapp that uses Railo
$ lein new lein-servlet struts foo   # " a Clojure/Java webapp that uses Struts1
```


### Regular configuration

The recommended use of `lein-servlet` is as a project-level plugin. Put
`[lein-servlet "0.2.0"]` into the `:plugins` vector of your `project.clj`.

If you must use this as a user-level plugin, put `[lein-servlet "0.2.0"]`
into the `:plugins` vector of your `:user` profile, or if you are on
Leiningen 1.x do `lein1 plugin install lein-servlet 0.2.0`.

For a detail list of all possible configuration options please check the file
`sample.project.clj`.


### Command-line usage

To view the configured servlet engine:

```bash
$ lein servlet engine
```

To start all configured webapps using the configured servlet engine:

```bash
$ lein servlet run  # opens server homepage in browser
```

To generate a WAR file:

```bash
$ lein servlet war     # generates target/<filename>.war file with sources only
$ lein servlet uberwar # generates target/<filename>.war file with dependencies
```


## Getting in touch

Leiningen mailing list: https://groups.google.com/group/leiningen/

Twitter: https://twitter.com/kumarshantanu

E-mail: kumar.shantanu(at)gmail.com


## License

Copyright © 2012 Shantanu Kumar

Distributed under the Eclipse Public License, the same as Clojure.
