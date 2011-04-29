@start java -Djava.security.policy=my.policy -jar DA1.jar 10  3 localhost 4000 true true
timeout 2
@start java -Djava.security.policy=my.policy -jar DA1.jar  3  6 localhost 2000 true false
@start java -Djava.security.policy=my.policy -jar DA1.jar  6  4 localhost 2000 true false
java -Djava.security.policy=my.policy -jar DA1.jar  4 10 localhost 2000 true false
pause
