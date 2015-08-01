# closp 

![Build Status](http://sveri.de:8082/buildStatus/icon?job=closp)

A Leiningen template combining luminus, chestnut plus some goodies.

## Goals
* Provide a full stack to get started with
* Provide generated code which can be changed easily
* Provide an opiniated predefined set of libraries
* Easily start side projects

## Differences to luminus

* [System] (https://github.com/danielsz/system) integration from the start - wrapper on top of sierras components
* User management with login/logout/registration and email activation (provided by postal)
* Conditional Reader support
* [closp-crud](https://github.com/sveri/closp) integration
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
2. Run `lein joplin migrate sqlite-dev-env` in the newly created project (This will add an admin user with username: 
_admin@localhost.de_ and password: _admin_ to a new database)
3. Run `lein figwheel` to start figwheel and compile the clojurescript.
4. Run `lein rel-jar` to generate a runnable jar file.

## Features
* closp-crud integration
* H2 database on filesystem as a default
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

## Database

Closp per default is configured to connect to a file H2 database.  
Additionally I added support for joplin <https://github.com/juxt/joplin> to handle migration of sql scripts.
To get started run `lein joplin migrate sql-dev-env` in the project folder. This is enough to get running.
Changing the jdbc url in the *closp.edn* file will switch to another database. But keep in mind you will have to 
run the migration step again and change the jdbc url in the project.clj too.  
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

Currently the support is some kind of tricky regarding support of latest firefox versions. Please look in the 
`profiles->dev->ddependencies` section of the `project.clj` file for some comments on this matter. It is possible to use
both, the htmlunitdriver and an older firefox version or only a newer firefox version.

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

* adding reframe example
* Whatever seems useful in the future.

## FAQ

### Could not find template closp on the classpath.

This occurs when you run `lein new closp ...` with an older leiningen version. Please upgrade to the latest one.

### I get this warning: Uncaught Error: Invariant Violation: _registerComponent(...): Target container is not a DOM element.

This will happen only in dev mode for every page where you did not explicitly register your clojurescript with.
Look at dev.cljs for this line `:jsload-callback (fn [] (core/main))` and change the call to `(core/main)` how you
need it for the page you are working on right now.

### I get this error in the javascript console: WebSocket connection to 'ws://localhost:9001/' failed: Error in connection establishment: net::ERR_CONNECTION_REFUSED

This happens because per default the browser-repl is not loaded. If you load like in:
<https://github.com/plexus/chestnut/blob/master/src/leiningen/new/chestnut/env/dev/cljs/chestnut/main.cljs> this error
will go away, however. After starting your dev system you will switch into the cljs repl and not be able anymore
to restart your components with `(restart)`. So this is a tradeoff one has to make.  
I decided to turn the browser-repl off because the clojurescript reloading still works in this setup. You only get this
error.

### When I change a route definition, the change is not applied after a page reload

You have to reset the system, by calling `({{ns}}.user/reset)` in the repl.

### I included prismatic/schema and on repl start I get an compile error

The error looks like this:  
`#<CompilerException java.lang.RuntimeException: No such var: sm/protocol, compiling:(plumbing/fnk/schema.clj:22:13)>`  
The problem is that ring-transit imports `schema/plumbing` which interfers with schema. Please look here for a quick solution
and explanation: <https://github.com/Prismatic/schema/issues/194

## Changes

### 0.2.0



### 0.1.20

* Users in admin view are ordered by username
* Fix #7 and #8
* Adding for test2junit
* switching db.users to db.user
* Adding example database test
* Integrating clj webdriver and adding several tests for admin and user interface
* Adding alias for unit and integtest
* Updating dependencies

    [ring "1.4.0"]  
    [compojure "1.4.0"]  
    [selmer "0.8.5"]    
    [buddy/buddy-auth "0.6.0"]  
    [buddy/buddy-hashers "0.6.0"]  
    [korma "0.4.2"]   
    [org.xerial/sqlite-jdbc "3.8.10.1"]  
    [datascript "0.11.6"]  
    [ring/ring-devel "1.4.0"]  
    [pjstadig/humane-test-output "0.7.0"]  


### 0.1.19

* Fixing broken 0.1.8 release

### 0.1.18

* bugfix for smtp configuration when using sendmail
* Upgrade closp-crud to 0.1.3
* provide user sqls for h2 and sqlite
* sqlite as default DB

### 0.1.17

* Bugfix regarding self registration
* Added closp-crud definition file for user (not used yet, just provided for reference)
* Recaptcha configuration now available in closp.edn, no need to edit the source anymore
* Adding bootstrap 3.3.5
* Providing react.js (0.12.1) and jquery (2.0.3) as local files instead of cdn provided (FIX #6)

### 0.1.16

* Adding back reset bugfix

### 0.1.15

* Removing piggyback and nrepl dependency
* Changes on how to use fighweel

### 0.1.14

* Removing weasel dependency
* Removing cljx support
* Introducing clojure-1.7.0-RC1 with support for cljc
* Several version updates
* Minimal CLJC example

### 0.1.13

* Version closp-crud -> 0.1.1

### 0.1.12

* Bugfix [#3](/../../issues/3)

### 0.1.11

* Upgrading Figwheel to 0.2.6
* Integrating closp-crud

### 0.1.10

* User can be deleted now
* Fixing defect with flash messsage
* Minor refactoring

### 0.1.9.1

* Fixing minor errors

### 0.1.9

* Self registration is now optional

### 0.1.8

* Displaying error message on user registration when mailserver is not working.
* Adding active link for top menu

### 0.1.7

* Changing _users_ table to _user_
* Adding option to pass in a config via system environment

### 0.1.6

* Switching from ragtime to joplin (which uses ragtime internally)

### 0.1.5

* Adding flash div in base html for flash support
* Exposing uuid in admin view instead of database id

### 0.1.4
 
* Adding recaptcha for signup form
* Adding generated README

 

## License

Copyright © 2015 Sven Richter

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
