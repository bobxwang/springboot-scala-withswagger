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

# Special Point 

* ProtostuffHttpMessageConverter 
  > 使用**io.protostuff**来进行数据传输

* ObservableReturnValueHandler 
  > 继承**AsyncHandlerMethodReturnValueHandler**类, 使**spring mvc**可以返回Observable<?>类型, 这样可以避免**web server**的连接池被占用而引起性能问题,增加服务器的吞吐量

*  MDCSimpleAsyncTaskExecutor/MDCCallableProcessingInterceptor/MdcPropagatingOnScheduleAction
  > 像**spring mvc**返回**Callable/DeferredResult**等类型时,本质上都是为了避免**web server**线程池被占用,利用非web的服务线程来处理,这个时候如果我们使用了**slf4j**中的**MDC**类时或者**jdk**的**threadlocal**时, 我们需要将请求线程中的一些数据给传递下去, 像**traceid**这种全链路调用标识符, 那我们就需要扩展**CallableProcessingInterceptorAdapter**跟**DeferredResultProcessingInterceptorAdapter**这类型, 在线程执行前后将当前线程的数据给传递到新起来的线程中

* others 
  > 如果是**spring cloud**中, 那我们也可以在跟其它服务进行交互时重写一些, 像**feignClient**中的**RequestInterceptor**拦截器, 在这添加我们自己的特殊处理 

