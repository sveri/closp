# closp

A Leiningen template for a full featured web framework.
  
See it live at: <http://sveri.de:3124>  
Username: admin@localhost.de  
Password: admin  

## Rationale

Starting sideprojects in web development for fun I find myself repeating the same patterns over and over again.
While luminus <http://www.luminusweb.net/> and chestnut <https://github.com/plexus/chestnut> provide a great start
they miss some features for me that I do again and again (authentication / signup processes / ...).  
So I pulled together the libraries I use to provide a general and opinionated starting point with at least trouble
as possible (at least that's the goal).

## Goals
* Provide a full stack to get started with
* Provide generated code which can be changed easily
* Provide an opiniated predefined set of libraries
* Easily start side projects

## Usage

1. Run `lein new closp _projectname_ -n foo.bar` in a different folder
2. Run `lein joplin migrate sql-dev-env` in the newly created project (This will add an admin user with username: 
_admin@localhost.de_ and password: _admin_ to a new database)
3. Run `lein cljx once` to compile the cljx files
4. Run `lein repl` and then `(start-dev-system)` to run the application in dev mode. 
This will also compile the clojurescript.
5. Run `lein rel-jar` to generate a runnable jar file.

## Features
* H2 database on filesystem as a default
* Joplin for database migrations
* Selmer as templating solution
* http-kit as a server
* cljx support
* Figwheel with clojurescript live reloading
* Reloading support for templates and clojure code
* Configuration with nomad
* User management with login/logout/registration with email activation (provided by postal)
* Recaptcha support for signup form
* Authentication provided by buddy
* reagent and datascript on frontend side
* Ring Antiforgery middleware
* Clojure miniprofiler example
* Componentized application
* Datascript with reagent example
* Booststrap css styles

## Docker

There is a dockerfile attached which will fetch the latest version and run an example project.

## Configuration

There is a closp.edn file in the resources folder  which shoud be adapted accordingly.  
I am not sure yet how to pass in runtime configuration. The current approach is for development only.  
Closp uses nomad <https://github.com/james-henderson/nomad> so using one of the descriped approaches there should be 
sufficient.  
There is also a configuration component where one coud add one for production environment.

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

The signup form is protected by recaptcha. To make it work:
* add your public key to signup.html
* Add your private key and domain to routes/user.clj#connectReCaptch

## Admin user interface

Closp ships with an administrator interface (/admin/users) to activate / deactivate users and set roles accordingly.
There is also an option to add new users.

## Reloading of clojure code and templates

In dev mode changes the clojure code will be recompiled and reloaded on page refresh. The same is true for the templates.
Theoretically this results development without server restarts.

## CLJX support

Closp will automatically compile cljx <https://github.com/lynaghk/cljx> files in the `cljx` folder and add them to 
the clj / cljs classpath.

## Clojurescripth with figwheel

When running in dev mode cljs files will be auto compiled and sent to the browser via figwheel 
<https://github.com/bhauman/lein-figwheel>

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

## Minor features.

* Miniprofiler <https://github.com/yeller/clojure-miniprofiler> example in `routes\user.clj -> admin-page function`. 
The profiler is enabled in development only
* Namspace support: Add `-n name.space` option to `lein new closp projectname` to provide a namespace for the source 
files.
* Support for flash messages with global flash div

## Planned features

* CRUD plugin to generate frontend to database CRUD for entities
* Whatever seems useful in the future.

## FAQ
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

## Changes
### 0.1.7

* Changing _users_ table to _user_

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
