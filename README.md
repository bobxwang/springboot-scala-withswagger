# Description 

* a basic guide using spring boot in scala combine swagger
* update jdk version to 1.8, for using lambda
* action can return the rx.Observable, see the **RxJavaController**

# Prerequisite

* [Spring-boot](http://projects.spring.io/spring-boot)
* [Scala](http://www.scala-lang.org)
* [Swagger](http://swagger.io)
* [Maven](http://maven.apache.org)
* [Docker](http://www.docker.com)
* [RxJava](https://github.com/ReactiveX/RxJava)

# Maven/sbt
why this using maven not sbt,just for convenient because we can inhert 'spring-boot-starter-parent',
so everything we no need to care the jar version. also it is ok for using sbt to manage the package.

# Structure
* aop, around every request and response 
* config, deploy the property for app
* controller, handle the request and return response
* exception, where to handle overall exception 
* utils, some useful function, like distribute lock and expend method
