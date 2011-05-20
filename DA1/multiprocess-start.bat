@start java -Djava.security.policy=my.policy -jar DA1.jar 0 localhost 7 2 1 4000 true
timeout 2
@start java -Djava.security.policy=my.policy -jar DA1.jar 1 localhost 7 2 0 2000 false
@start java -Djava.security.policy=my.policy -jar DA1.jar 2 localhost 7 2 0 2000 false
@start java -Djava.security.policy=my.policy -jar DA1.jar 3 localhost 7 2 0 2000 false
@start java -Djava.security.policy=my.policy -jar DA1.jar 4 localhost 7 2 2 2000 false
@start java -Djava.security.policy=my.policy -jar DA1.jar 5 localhost 7 2 0 2000 false
java -Djava.security.policy=my.policy -jar DA1.jar 6 localhost 7 2 0 2000 false
pause
