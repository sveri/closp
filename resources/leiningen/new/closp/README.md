# {{name}}

Your new project at <http://localhost:3000>

Username: admin@localhost.de  
Password: admin  

## Usage

1. Run `lein joplin migrate sqlite-dev-env` in the newly created project (This will add an admin user with username:
_admin@localhost.de_ and password: _admin_ to a new database)
2. Run `lein repl` and then `(start-dev-system)` to run the application in dev mode.
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
* User management with login/logout/registration with email activation (provided by postal)
* Authentication provided by buddy
* reagent and datascript on frontend side
* Ring Antiforgery middleware
* Clojure miniprofiler example
* Componentized application
* Datascript with reagent example
* Booststrap css styles
* Example for clj-webdriver tests

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

## Support for flash messages with global flash div
To use it call (layout/flash-result "success message" "alert-success") and on the next page load
a div will appear with the success message on top of the page.

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

## Minor features.

* Miniprofiler <https://github.com/yeller/clojure-miniprofiler> example in `routes\user.clj -> admin-page function`. 
The profiler is enabled in development only 
* Namspace support: Add `-n name.space` option to `lein new closp projectname` to provide a namespace for the source 
files.
* Self registration can be turned on or off in the closp.edn file in the resources folder.

## Planned features

* CRUD plugin to generate frontend to database CRUD for entities
* Whatever seems useful in the future.

## FAQ
### I get this warning: Uncaught Error: Invariant Violation: _registerComponent(...): Target container is not a DOM element.

This will happen only in dev mode for every page where you did not explicitly register your clojurescript with.
Look at dev.cljs for this line `:jsload-callback (fn [] (core/main))` and change the call to `(core/main)` how you
need it for the page you are working on right now.
 

## License

Copyright © 2015 Sven Richter

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
