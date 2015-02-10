# closp

A Leiningen template for a full featured web framework (whatever that means)

## Usage

Until the first version in clojars appears follow this:

1. Clone this project
2. Run `lein install` in the project folder
3. Run `lein new closp _projectname_ -n _your.ns.here_` in a different folder
4. Run `lein ragtime migrate` in the newly created project (This will add an admin user with username: 
_admin@localhost.de_ and password: _admin_ to a new database)
5. Run `lein cljx once` to compile the cljx files
6. Run `lein repl` and the `(start-server)` to run the application in dev mode. This will also compile the clojurescript.
7. Run `lein rel-jar` to generate a runnable jar file.



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
* Componentized application (https://github.com/danielsz/system“

## FAQ
### I get this warning: Uncaught Error: Invariant Violation: _registerComponent(...): Target container is not a DOM element.

This will happen only in dev mode for every page where you did not explicitly register your clojurescript with.
Look at dev.cljs for this line `:jsload-callback (fn [] (core/main))` and change the call to `(core/main)` how you
need it for the page you are working on right now.
 

## License

Copyright © 2015 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
