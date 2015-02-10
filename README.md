# closp

A Leiningen template for a full featured web framework.
  
See it live at: http://sveri.de:3124

## Usage

1. Run `lein new closp _projectname_ -n _your.ns.here_` in a different folder
2. Run `lein ragtime migrate` in the newly created project (This will add an admin user with username: 
_admin@localhost.de_ and password: _admin_ to a new database)
3. Run `lein cljx once` to compile the cljx files
4. Run `lein repl` and then `(start-dev-system)` to run the application in dev mode. 
This will also compile the clojurescript.
5. Run `lein rel-jar` to generate a runnable jar file.

## Features
* H2 database on filesystem as a default
* Ragtime for database migrations
* Selmer as templating solution
* http-kit as a server
* cljx support
* Figwheel with clojurescript live reloading
* Reloading support for templates and clojure code
* Configuration with nomad
* User management with login/logout/registration with email activation (provided by postal) (WIP)
* Authentication provided by buddy (WIP)
* reagent and datascript on frontend side (WIP)
* Ring Antiforgery middleware (https://github.com/weavejester/ring-anti-forgery)
* Clojure miniprofiler example (https://github.com/yeller/clojure-miniprofiler)
* Componentized application (https://github.com/danielsz/system)
* Datascript with reagent example taken from https://gist.github.com/allgress/11348685

## Docker

There is a dockerfile attached which will fetch the latest version and run an example project.

## FAQ
### I get this warning: Uncaught Error: Invariant Violation: _registerComponent(...): Target container is not a DOM element.

This will happen only in dev mode for every page where you did not explicitly register your clojurescript with.
Look at dev.cljs for this line `:jsload-callback (fn [] (core/main))` and change the call to `(core/main)` how you
need it for the page you are working on right now.
 

## License

Copyright © 2015 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
