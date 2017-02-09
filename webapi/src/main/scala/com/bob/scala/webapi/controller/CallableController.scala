package com.bob.scala.webapi.controller

import java.util.concurrent.{Callable, Executors, TimeUnit}

import com.bob.java.webapi.spextension.MDCAwareCallable
import com.bob.scala.webapi.service.HelperService
import org.slf4j.{Logger, LoggerFactory, MDC}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.{PathVariable, RequestMapping, RequestMethod, RestController}
import org.springframework.web.context.request.async.{DeferredResult, WebAsyncTask}

/**
  * 异步例子,返回callable,deferredresult,webasynctask
  *
  * Created by bob on 17/2/4.
  */
@RestController
@RequestMapping(value = Array("callable/v1"))
class CallableController {

  private val LOGGER: Logger = LoggerFactory.getLogger(getClass)

  @Autowired
  private val helperService: HelperService = null

  @RequestMapping(value = Array("rs/nocallable/{name}"), method = Array(RequestMethod.GET))
  def nocallable(@PathVariable("name") name: String): String = {
    helperService.handlerInput(name)
  }

  @RequestMapping(value = Array("rs/callable/{name}"), method = Array(RequestMethod.GET))
  def callable(@PathVariable("name") name: String): Callable[String] = {
    LOGGER.info(s"rs/callable/${name} begin to process}")
    new Callable[String] {
      def call() = {
        val r = helperService.handlerInput(name)
        LOGGER.info(s"rs/callable/${name} stop to process}")
        r
      }
    }
  }

  @RequestMapping(value = Array("rs/defferredrs/{name}"), method = Array(RequestMethod.GET))
  def deferredResult(@PathVariable("name") name: String): DeferredResult[String] = {
    val differredrs = new DeferredResult[String](4000L)
    LOGGER.info(s"rs/defferredrs/${name} begin to process}")
    LongTimeAsyncCallService.makeRemoteCallAndUnknownWhenFinish(new LongTimeTaskCallback(name) {
      def callback(result: Any) = {
        LOGGER.info(s"rs/defferredrs/${name} stop to process}")
        differredrs.setResult(result.toString)
      }
    })
    differredrs.onTimeout(new Runnable {
      def run() = differredrs.setResult(s"${name} is timeout")
    })
    differredrs
  }

  @RequestMapping(value = Array("rs/webasynctask/{name}"), method = Array(RequestMethod.GET))
  def webAsyncTask(@PathVariable("name") name: String): WebAsyncTask[String] = {
    LOGGER.info("/rs/webasynctask begin to process")
    new WebAsyncTask[String](new Callable[String] {
      def call() = {
        val r = helperService.handlerInput(name)
        LOGGER.info("rs/webasynctask stop to process")
        r
      }
    })
  }

  abstract class LongTimeTaskCallback(val parm: Any) {
    def callback(result: Any)
  }

  object LongTimeAsyncCallService {
    private val scheduler = Executors.newScheduledThreadPool(4)

    def makeRemoteCallAndUnknownWhenFinish(callback: LongTimeTaskCallback) {
      scheduler.schedule(MDCAwareCallable.wrap(new Runnable {
        def run() = {
          callback.callback(s"${helperService.handlerInput(callback.parm.toString)} is done")
        }
      }, MDC.getCopyOfContextMap), 1, TimeUnit.SECONDS)
    }
  }

}