# Description 

a basic guide using springboot in scala combine swagger

# Preposition

* [Spring-boot](http://projects.spring.io/spring-boot)
* [Scala](http://www.scala-lang.org)
* [Swagger](http://swagger.io)
* [Maven](http://maven.apache.org)
* [Docker](http://www.docker.com)

# Maven/sbt
why this using maven not sbt,just for convenient because we can inhert 'spring-boot-starter-parent',
so everything we no need to care the jar version. also it is ok for using sbt to manage the package.

# Structure
* aop, around every request and response 
* config, deploy the property for app
* controller, handle the request and return response
* exception, where to handle overall exception 
* utils, some useful function, like distribute lock and expend method