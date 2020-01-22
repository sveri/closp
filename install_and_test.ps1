Remove-Item -Recurse -Force C:\Users\sveri\.m2\repository\closp
clj -A:jar
clj -A:install
cd ..\temp
Remove-Item -Recurse -Force closptest
clj -A:new closp de/sveritest/closptest
cd ..\closp