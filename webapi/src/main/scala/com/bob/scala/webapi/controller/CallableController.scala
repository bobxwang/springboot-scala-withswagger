package com.bob.scala.webapi.controller

import java.util.concurrent.{Callable, Executors, TimeUnit}

import org.slf4j.{Logger, LoggerFactory}
import org.springframework.web.bind.annotation.{PathVariable, RequestMapping, RequestMethod, RestController}
import org.springframework.web.context.request.async.{DeferredResult, WebAsyncTask}

/**
  * Created by bob on 17/2/4.
  */
@RestController
@RequestMapping(value = Array("callable/v1"))
class CallableController {

  val LOGGER: Logger = LoggerFactory.getLogger(getClass)

  @RequestMapping(value = Array("rs/callable/{name}"), method = Array(RequestMethod.GET))
  def callable(@PathVariable("name") name: String): Callable[String] = {
    LOGGER.info(s"rs/callable/${name} begin to process}")
    new Callable[String] {
      def call() = {
        Thread.sleep(2000)
        LOGGER.info(s"rs/callable/${name} stop to process}")
        s"${name} is returned"
      }
    }
  }

  @RequestMapping(value = Array("rs/defferredrs/{name}"), method = Array(RequestMethod.GET))
  def deferredResult(@PathVariable("name") name: String): DeferredResult[String] = {
    val differredrs = new DeferredResult[String](1000L)
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
        Thread.sleep(3000)
        LOGGER.info("rs/webasynctask stop to process")
        s"${name} is invoked done"
      }
    })
  }

  abstract class LongTimeTaskCallback(val parm: Any) {
    def callback(result: Any)
  }

  object LongTimeAsyncCallService {
    private val scheduler = Executors.newScheduledThreadPool(4)

    def makeRemoteCallAndUnknownWhenFinish(callback: LongTimeTaskCallback) {
      scheduler.schedule(new Runnable {
        def run() = {
          Thread.sleep(2000)
          callback.callback(s"${callback.parm.toString} is done")
        }
      }, 3, TimeUnit.SECONDS)
    }
  }

}