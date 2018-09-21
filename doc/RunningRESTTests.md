# REST tests

There are a few REST tests included with closp that test the users api. 

To run them execute:  `lein test :rest`  
  
`:rest` is an identifier defined in the project.clj of the root file and executes all tests marked as `^:rest`.  
You can find the tests in `integtest/clj/your/ns`.