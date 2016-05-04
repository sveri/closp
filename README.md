# closp 

![Build Status](http://sveri.de:8082/buildStatus/icon?job=closp)

[![Join the chat at https://gitter.im/sveri/closp](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/sveri/closp?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

A Leiningen template combining luminus, chestnut plus some goodies.

Mailing List: [![Clojureverse](https://rawgit.com/clojureverse/clojureverse-assets/master/clojureverse-org-green.svg)](http://clojureverse.org/c/closp)

![Intro Gif](intro.gif?raw=true "Intro")

These five steps are all it takes to get up and running.

[Documentation with Tutorial](http://closp.net/)


## Goals
* Provide a full stack to get started with
* Provide generated code which can be changed easily
* Provide an opiniated predefined set of libraries
* Easily start side projects

## Differences to luminus

* [System] (https://github.com/danielsz/system) integration from the start - wrapper on top of sierras components
* User management with login/logout/registration and email activation (provided by postal)
* Conditional Reader support
* [closp-crud](https://github.com/sveri/closp-crud) integration
* Live reloading for both clojurescript and clojure out of the box.
* Clojure miniprofiler enabled.
* Recaptcha for Signup

## Rationale

Starting sideprojects in web development for fun I find myself repeating the same patterns over and over again.
While luminus <http://www.luminusweb.net/> and chestnut <https://github.com/plexus/chestnut> provide a great start
they miss some features for me that I do again and again (authentication / signup processes / ...).  
So I pulled together the libraries I use to provide a general and opinionated starting point with at least trouble
as possible (at least that's the goal).

## Usage

1. Run `lein new closp _projectname_ -n foo.bar` in a different folder
2. Run `lein migrate` in the newly created project (This will add an admin user with username: 
_admin@localhost.de_ and password: _admin_ to a new database)
3. Run `lein figwheel` to start figwheel and compile the clojurescript.
4. Run `lein rel-jar` to generate a runnable jar file.

## Features
* closp-crud integration
* SQlite database on filesystem as a default
* Joplin for database migrations
* Selmer as templating solution
* http-kit as a server
* cljc support
* Figwheel with clojurescript live reloading
* Reloading support for templates and clojure code
* Configuration with nomad
* User management with login/logout/registration and email activation (provided by postal)
* Recaptcha support for signup form
* Authentication provided by buddy
* reagent and datascript on frontend side
* Ring Antiforgery middleware
* Clojure miniprofiler example
* Componentized application
* Datascript with reagent example
* Booststrap css styles
* Example for clj-webdriver tests
* Internationalization support with tower
  

## Showcase

See it live at: <http://sveri.de:3124>  
Username: admin@localhost.de  
Password: admin  

## Docker

There is a dockerfile attached which will fetch the latest version and run an example project.

## Configuration

There is a closp.edn file in the resources folder  which should be adapted accordingly.  
Closp uses nomad <https://github.com/james-henderson/nomad>, so you can configure everything as you can do with nomad.  
When you start your project from the repl it will load the default `closp.edn` from `resources` folder, which fits
for development.  
For a different config in another environment you can pass in a file path via system environment setting like so:
`java -jar -Dclosp-config-path=C:\\path\\to\\iwf-prod.edn closp.jar`.  
Please think of changing the :env key in the config to :prod instead of :dev when changing to a different
environment.

## closp-crud

This is a module that generates html, routing and sql files for a given table definition. For more
information please look here: <https://github.com/sveri/closp>.  
Short Intro:  

* Create a new definition in env/dev/entities/example.edn (Look at env/dev/entities/user.edn for an example)
* Run lein run -m de.sveri.clospcrud.closp-crud/closp-crud -f env/dev/entities/example.edn
* Run lein migrate to create the new database
* Add the new routes handler to components.handler namespace
* Reset the server
* Browse to /example

## Database

Closp per default is configured to connect to a file SQlite database.  
Additionally I added support for joplin <https://github.com/juxt/joplin> to handle migration of sql scripts.
To get started run `lein migrate` in the project folder. This is enough to get running.
Changing the jdbc url in the *closp.edn* file will switch to another database. But keep in mind you will have to 
run the migration step again and change the jdbc url in the `joplin.edn` too.  
The connection is handled by jdbc <https://github.com/clojure/java.jdbc> so everything that jdbc supports is supported 
by closp out of the box.  
Closp comes with korma <https://github.com/korma/Korma> for an abstraction layer over jdbc. See `db\users.clj` for
how it is used.

## Authentication and Authorization

We use the buddy (<https://github.com/funcool/buddy>) library for this.  
Configuration is done in _ns.service.auth_  
There is a concept of roles, _admin_ and _none_ are alreaded provided, you can add more in the auth namespace.
Or, create a database storage for this.  
Next you can find a _rules_ def in the _auth_ namespace which defines the access rules for every available link. For
more information please look at the buddy documentation.
  
## Templating

Closp ships with selmer <https://github.com/yogthos/Selmer> (django inspired) templating solution.

## Signup

There is a signup workflow implemented that sends out an email after regristration with a link to activate the account.
Until the account is activated the user won't be able to login.

## Recaptcha

The signup form is protected by recaptcha. To make it work open your closp.edn file and fill these values properly:
* :captcha-public-key
* :private-recaptcha-key
* :recaptcha-domain


## Admin user interface

Closp ships with an administrator interface (/admin/users) to activate / deactivate users and set roles accordingly.
There is also an option to add new users.

## Reloading of clojure code and templates

In dev mode changes the clojure code will be recompiled and reloaded on page refresh. The same is true for the templates.
Theoretically this results development without server restarts.

## Clojurescripth with figwheel

When running in dev mode cljs files will be auto compiled and sent to the browser via figwheel 
<https://github.com/bhauman/lein-figwheel>.  
If you want to autoload a different cljs function you have to adapt dev.cljs and the project.clj file at 
[:cljsbuild :dev :figwheel].

## Email system

Closp uses postal <https://github.com/drewr/postal> for sending authentication links. This can be configured in closp.edn.

## Components

Closp comes with some predefined components <https://github.com/danielsz/system>  

* Handler component
* Configuration component
* Database component
* Webserver component

To restart the components just hit `(reset)` in the running repl.

## Ring antiforgery 

<https://github.com/weavejester/ring-anti-forgery> is enabled per default for every shipped form.
If you use ajax post / put / ... calls you need to provide a :X-CSRF-Token in the header. With cljs-ajax for example
it would look like this:  

    (ajax/ajax-request
        {:uri             url
         :method          method
         :params          content
         :headers         {:X-CSRF-Token (get-value "glob_anti_forgery")}})
         
## Transit and cljs-ajax

There is an example on how to use cljs-ajax for doing ajax requests to the server. The request will use transit as
a transport format. You can find that at the uri: `"/ajax/page/init"` and in the `ajax.cljs` file.  
You need to run `lein figwheel` at least once to compile the clojurescript.
And finally open the `dev.cljs` namespace and change the requiring namespace from  
 `(:require [f.d.core :as core])` to `(:require [f.d.ajax :as core])`

## Reagent and Datascript

Closp includes a reagent <https://github.com/reagent-project/reagent> and datascript 
<https://github.com/tonsky/datascript> example taken from <https://gist.github.com/allgress/11348685> to get started
with frontend development.

## Production

There is a leiningen task defined in the _project.clj_ to generate an uberjar. Just execute `lein uberjar`.  
By default this will include your closp.edn config file in the build from resources folder. You should at least change
the :env entry to :prod or something else than :dev.  
There are several ways to setup a more separated dev / staging / prod environment. Please lookup nomad for that.

## CLJ-Webdriver

Closp comes with some examples on how to use clj webdriver in your projects for integration tests. They reside in
`integtest\clj`.

Per default the tests are run with the :htlmunit driver, which is fast, but not that good on javascript. To change that,
open: {{ns}}.web.setup and adapt the driver in `browser-setup` to `:firefor` or `:chrome`.

## Internationalization

Closp uses <https://github.com/ptaoussanis/tower> for internationalization. It is configured as a component in 
 `your.ns.components.locale`. You have to add additional strings / translations there to use them in your web 
  application. For examples look at `your.ns.routes.user`.

## Minor features.

* Miniprofiler <https://github.com/yeller/clojure-miniprofiler> example in `routes\user.clj -> admin-page function`. 
The profiler is enabled in development only
* Namspace support: Add `-n name.space` option to `lein new closp projectname` to provide a namespace for the source 
files.
* Support for flash messages with global flash div
* Self registration can be turned on or off in the closp.edn file in the resources folder.
* Test2junit plugin to create parseable test results.

## Planned features

* Currently working on a webui for closp crud
* Whatever seems useful in the future.

## Contributors  

* Henrik Lundahl - https://github.com/henriklundahl

## FAQ

### Running `lein figwheel` fails with `...No such var: ana/forms-seq*...`

The complete error message is:
 
    clojure.lang.Compiler$CompilerException: java.lang.RuntimeException: No such var: ana/forms-seq*, compiling:(figwheel_sidecar/utils.clj:49:21)

This issue is tracked in https://github.com/sveri/closp/issues/20. According to the reporter upgrading to leiningen 
2.5.3 fixed it for him. If it does not for you, please reopen the issue.


### Could not find template closp on the classpath.

This occurs when you run `lein new closp ...` with an older leiningen version. Please upgrade to the latest one.

### I get this warning: Uncaught Error: Invariant Violation: _registerComponent(...): Target container is not a DOM element.

This will happen only in dev mode for every page where you did not explicitly register your clojurescript with.
Look at dev.cljs for this line `:jsload-callback (fn [] (core/main))` and change the call to `(core/main)` how you
need it for the page you are working on right now.

### When I change a route definition, the change is not applied after a page reload

You have to reset the system, by calling `({{ns}}.user/reset)` in the repl.

### I included prismatic/schema and on repl start I get an compile error

The error looks like this:  
`#<CompilerException java.lang.RuntimeException: No such var: sm/protocol, compiling:(plumbing/fnk/schema.clj:22:13)>`  
The problem is that ring-transit imports `schema/plumbing` which interfers with schema. Please look here for a quick solution
and explanation: <https://github.com/Prismatic/schema/issues/194


## [Changes](CHANGES.md) 

 

## License

Copyright © 2015 Sven Richter

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
