# closp 

**closp has been changed significantly in 0.4 and upwards.**

Get started with: `lein new closp -n yourprojectname -n name.space` 

Then open a repl and after startup enter: `(start-dev-system)` to startup the server.  
The clojurescript is compiled by running `lein figwheel` in the projects command line.

It comes with: 
- component
- postgresql as database
- re-frame as cljs library
- figwheel for reloading
- etaoin for frontend tests
- bidi for routing (back / frontend)
- buddy for login / logout (jwt token is used and the complete UI for login / logout is integrated in the template)
- tempura for i18n
- phrase for form validation


**Documentation** 

https://cljdoc.xyz/d/closp/lein-template


**Old documenation**

You can find the old documentation here: [OLD Readme](README_OLD.md)

[![CircleCI](https://circleci.com/gh/sveri/closp.svg?style=svg)](https://circleci.com/gh/sveri/closp)
 


## Supported by

The development of closp is supported by Jetbrains and their awesome IDE

[![Jetbrains Logo](jetbrains.svg)](https://www.jetbrains.com/)

 

## License

Copyright © 2018 Sven Richter

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
