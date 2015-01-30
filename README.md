# closp

A Leiningen template for a full featured web framework (whatever that means)

## Usage

Until the first version in clojars appears follow this:  
1. Clone this project
2. Run `lein install` in the project folder
3. Run `lein new closp _projectname_ -n _your.ns.here_` in a different folder

This will generate a new closp project in the `projectname` folder

## Features
* H2 database on filesystem as a default
* Selmer as templating solution
* User management with login/logout/registration with email activation (provided by postal)
* http-kit as a server
* Authentication provided by buddy (WIP)
* reagent and datascript on frontend side (WIP)

## License

Copyright © 2015 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
