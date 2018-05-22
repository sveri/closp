
First run: `lein new closp -n yourprojectname -n name.space` to create a new project `yourprojectname` with 
the namespace `name.space`. 

Then open a repl and after startup enter: `(start-dev-system)` to startup the server.  
The clojurescript is compiled by running `lein figwheel` in the projects command line.