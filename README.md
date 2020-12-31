# closp

## Usage

### Backend
1. Run `clj -A:new -M:new https://github.com/sveri/closp@d0aca65b2263cd6b2107d4b3c4dae2f12f7fb565 de/sveritest/closptest` in a console.
2. Open your postgresql instance and add a new user: _{{name}}_ with password: _{{name}}_ and create a database _{{name}}_.
3. Add a users table to the new database. The script can be found in _migrators/postgres/1-user.up.sql'_.
4. CD to the newly generated folder _{{name}}_.
5. Run `clj` and from the repl `(start)` to start the development server.

### Frontend
1. Run `npm install` in a separate console to install the dependencies.
2. Run `shadow-cljs watch main` to start the cljs compiler.
3. Navigate to http://localhost:3000/reagent-example.

### Preview

![Intro](intro.gif)


## Supported by

The development of closp is supported by Jetbrains and their awesome IDE

[![Jetbrains Logo](jetbrains.svg)](https://www.jetbrains.com/)

 

## License

Copyright © 2020 Sven Richter

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
