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

## License

Copyright © 2015 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
