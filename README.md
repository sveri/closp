# closp 

**closp has been changed significantly in 0.3 and upwards.** 

All the current documentation relates to the latest stable version 0.2.0.  
You can use the old version by executing: lein new closp --template-version 0.2.0

[Build Status](https://circleci.com/gh/sveri/closp.png?style=shield&circle-token=:circle-token)

[Slack Channel CLOSP](https://clojurians.slack.com/messages/closp)

A Leiningen template combining luminus, chestnut plus some goodies.

![Intro Gif](intro.gif?raw=true "Intro")

These five steps are all it takes to get up and running.

[Documentation with Tutorial](http://closp.net/) - please be aware that the documentation is for version 0.2.0


## Goals
* Provide a full stack to get started with
* Provide generated code which can be changed easily
* Provide an opiniated predefined set of libraries
* Easily start side projects

## Differences to luminus

* [System] (https://github.com/danielsz/system) integration from the start - wrapper on top of sierras components
* User management with login/logout/registration and email activation (provided by postal)
* Conditional Reader support
* crud integration via entities definitions
* Live reloading for both clojurescript and clojure out of the box.
* Recaptcha for Signup

## Rationale

Starting sideprojects in web development for fun I find myself repeating the same patterns over and over again.
While luminus <http://www.luminusweb.net/> and chestnut <https://github.com/plexus/chestnut> provide a great start
they miss some features for me that I do again and again (authentication / signup processes / ...).  
So I pulled together the libraries I use to provide a general and opinionated starting point with at least trouble
as possible (at least that's the goal).

## Usage

1. Run `lein new closp _projectname_ -n foo.bar` in a different folder.
2. Open your postgresql instance and add a new user: _projectname_ with password: _projectname_ and create a database _projectname_.
3. Add a users table to the new database. The script can be found in _migrators/postgres/1-user.up.sql'.
4. CD to the newly generated folder _projectname_.
5. Run `lein repl` and inside `(start-dev-system)` to start the server.
6. Run `lein figwheel` in a separate console to start figwheel and compile the clojurescript.

## Features
* closp-crud integration
* Postgresql database on filesystem as a default
* Hiccup as templating solution
* Immutant as a server
* cljc support
* Figwheel with clojurescript live reloading
* Reloading support for templates and clojure code
* User management with login/logout/registration
* Recaptcha support for signup form
* Authentication provided by buddy
* reagent and datascript on frontend side
* Ring Antiforgery middleware
* Componentized application
* Booststrap 4.0 css styles
* Example for HTML Frontend tests with etaoin
* Internationalization support with tempura
  

## Docker

There is a dockerfile attached which will fetch the latest version and run an example project.

## Configuration

There is a closp.edn file in the resources folder  which should be adapted accordingly.    
When you start your project from the repl it will load the default `closp.edn` from `resources` folder, which fits
for development.  
For a different config in another environment you can pass in a file path via system environment setting like so:
`java -jar -Dclosp-config-path=C:\\path\\to\\iwf-prod.edn closp.jar`.  
Please think of changing the :env key in the config to :prod instead of :dev when changing to a different
environment.

## Database

Closp per default is configured to connect to postgresql database.  
Changing the jdbc url in the *closp.edn* file will switch to another database.  
The connection is handled by jdbc <https://github.com/clojure/java.jdbc> so everything that jdbc supports is supported 
by closp out of the box.  

## Authentication and Authorization

We use the buddy (<https://github.com/funcool/buddy>) library for this.  
Configuration is done in _ns.service.auth_  
There is a concept of roles, _admin_ and _none_ are alreaded provided, you can add more in the auth namespace.
Or, create a database storage for this.  
Next you can find a _rules_ def in the _auth_ namespace which defines the access rules for every available link. For
more information please look at the buddy documentation.
  
## Templating

Closp ships with hiccup <https://github.com/weavejester/hiccup> templating solution.

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

## Clojurescript with figwheel

When running in dev mode cljs files will be auto compiled and sent to the browser via figwheel 
<https://github.com/bhauman/lein-figwheel>.  
If you want to autoload a different cljs function you have to adapt dev.cljs and the project.clj file at 
[:cljsbuild :dev :figwheel].

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

## Reagent

Closp includes a reagent <https://github.com/reagent-project/reagent> example under http://localhost:3000/reagent-example.

## Production

There is a leiningen task defined in the _project.clj_ to generate an uberjar. Just execute `lein rel-jar`.  
By default this will include your closp.edn config file in the build from resources folder. You should at least change
the :env entry to :prod or something else than :dev.  
There are several ways to setup a more separated dev / staging / prod environment. Please lookup nomad for that.

## Web Integration tests

Closp comes with some examples on how to use etaoin in your projects for integration tests. They reside in
`integtest\clj`.

Per default the tests are run with the :htlmunit driver, which is fast, but not that good on javascript. To change that,
open: {{ns}}.setup and adapt the driver in `browser-setup` to `:firefor` or `:chrome`.

## Internationalization

Closp uses <https://github.com/ptaoussanis/tempura> for internationalization.
Strings are internationalized in

* en.edn
* de.edn

Both can be found in `resources/i18n`

## Minor features.

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

### Could not find template closp on the classpath.

This occurs when you run `lein new closp ...` with an older leiningen version. Please upgrade to the latest one.

### I get this warning: Uncaught Error: Invariant Violation: _registerComponent(...): Target container is not a DOM element.

This will happen only in dev mode for every page where you did not explicitly register your clojurescript with.
Look at dev.cljs for this line `:jsload-callback (fn [] (core/main))` and change the call to `(core/main)` how you
need it for the page you are working on right now.

### When I change a route definition, the change is not applied after a page reload

You have to reset the system, by calling `({{ns}}.user/reset)` in the repl.


## Supported by

The development of closp is supported by Jetbrains and their awesome IDE

[![Jetbrains Logo](jetbrains.svg)](https://www.jetbrains.com/)

 

## License

Copyright © 2018 Sven Richter

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
