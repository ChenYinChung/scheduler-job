##Purpose
 Test JobScheduler HA  

## Requirements
JDK 11
Gradle 5

## Spring boot framework integrate Quartz

* java -jar schedule.jar
  
## Build two different jar with port 8080 & 9090  

* replacement server port in resources/application.properties 8080 & rebuild ＆ rename schedule-8080.jar
* replacement server port in resources/application.properties 9090 & rebuild ＆ rename schedule-9090.jar


## Run two different jar with port 8080 & 9090  

* gradle migration flyway initial data for postgre
* java -jar schedule-8080.jar
* java -jar schedule-9090.jar
