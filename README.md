# Description 

* A basic guide using spring boot in scala combine swagger
* Update jdk version to 1.8, for using lambda
* Action can return the rx.Observable, like return Callable Or DeferredResult see the **RxJavaController**
* When controller return Callable/DeferredResult/Observable, Support MDC value to deliver

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
