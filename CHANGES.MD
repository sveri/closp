
## Changes

### 0.2.1

* Captcha :captcha-enabled? key from configuration was ignored in _signup.html_
* Updating buddy-auth to 1.1.0
* Updating buddy-hashers to 1.0.0
* Minor refactorings


### 0.2.0

* **Breaking Change** The schema for closp-crud changed, for more see here: <https://github.com/sveri/closp-crud>
* Updating clojure to 1.8.0
* Updating closp-crud to 0.3.0

### 0.1.28 - Breaking change 

* **Breaking Change** Up to now the templates path was set for selmer. This has changed. In the templates itself
you have to add the templates path like this: `{% extends "templates/base.html" %}`. In the clojure files it is set
in the `layout/render` function. As long as you use this, your code can stay the same.
* **Breaking Change** Switched to closp-crud `0.2.0` which uses ids now instead of UUIDs
* More work on closp crud admin


### 0.1.27

* Updating timbre to [com.taoensso/timbre "4.1.4"]
* Removing ring-server for dev. Replace by http-kit with wrap-reload
* Updating clj-webdriver to 0.7.2
* Updating joplin to 0.3.6 - *breaking changes*
* Fix #16 #17 #18 #19

### 0.1.26
* Updating lein-cljsbuild to 1.1.1
* Bugfix in cljs ns

### 0.1.25

* adapt .gitignore to include _js_ folder
* Updating closp-crud to 0.1.4
* Updating cljs to "1.7.170"
* Updating figwheel to "0.5.0-2"
* Updating cljs-uuid to [com.lucasbradstreet/cljs-uuid-utils "1.0.2"]
* Updating datascript to [datascript "0.13.3"]
* still working on cc - alpha status

### 0.1.24

* Namespace fixes
* Fixes with test setup

### 0.1.23

* Preparing for closp-crud webui
* Updating reagent to 0.5.1
* Adding cljs-ajax 0.3.14
* adding ring transit 0.1.3
* Added clojurescript example on how to use transit and ajax-cljs with a simple get request

### 0.1.22

* Updating joplin to 0.2.17 because of a defect interfering with leiningen 2.5.2
* Introducing core.typed

### 0.1.21

* Bugfix in test namespace

### 0.1.20

* Users in admin view are ordered by username
* Fix #7 and #8
* Adding for test2junit
* switching db.users to db.user
* Adding example database test
* Integrating clj webdriver and adding several tests for admin and user interface
* Adding alias for unit and integtest
* Updating dependencies

    [ring "1.4.0"]  
    [compojure "1.4.0"]  
    [selmer "0.8.5"]    
    [buddy/buddy-auth "0.6.0"]  
    [buddy/buddy-hashers "0.6.0"]  
    [korma "0.4.2"]   
    [org.xerial/sqlite-jdbc "3.8.10.1"]  
    [datascript "0.11.6"]  
    [ring/ring-devel "1.4.0"]  
    [pjstadig/humane-test-output "0.7.0"]  



### 0.1.19

* Fixing broken 0.1.8 release

### 0.1.18

* bugfix for smtp configuration when using sendmail
* Upgrade closp-crud to 0.1.3
* provide user sqls for h2 and sqlite
* sqlite as default DB

### 0.1.17

* Bugfix regarding self registration
* Added closp-crud definition file for user (not used yet, just provided for reference)
* Recaptcha configuration now available in closp.edn, no need to edit the source anymore
* Adding bootstrap 3.3.5
* Providing react.js (0.12.1) and jquery (2.0.3) as local files instead of cdn provided (FIX #6)

### 0.1.16

* Adding back reset bugfix

### 0.1.15

* Removing piggyback and nrepl dependency
* Changes on how to use fighweel

### 0.1.14

* Removing weasel dependency
* Removing cljx support
* Introducing clojure-1.7.0-RC1 with support for cljc
* Several version updates
* Minimal CLJC example

### 0.1.13

* Version closp-crud -> 0.1.1

### 0.1.12

* Bugfix [#3](/../../issues/3)

### 0.1.11

* Upgrading Figwheel to 0.2.6
* Integrating closp-crud

### 0.1.10

* User can be deleted now
* Fixing defect with flash messsage
* Minor refactoring

### 0.1.9.1

* Fixing minor errors

### 0.1.9

* Self registration is now optional

### 0.1.8

* Displaying error message on user registration when mailserver is not working.
* Adding active link for top menu

### 0.1.7

* Changing _users_ table to _user_
* Adding option to pass in a config via system environment

### 0.1.6

* Switching from ragtime to joplin (which uses ragtime internally)

### 0.1.5

* Adding flash div in base html for flash support
* Exposing uuid in admin view instead of database id

### 0.1.4
 
* Adding recaptcha for signup form
* Adding generated README