package com.bob.scala.webapi.aop

import java.lang.reflect.Method

import com.fasterxml.jackson.databind.ObjectMapper
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.{Aspect, Around}
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.{LoggerFactory, Logger}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.bob.scala.webapi.utils.StringImplicit.RichFormatter

/**
 * Created by bob on 16/2/29.
 */
@Component
@Aspect
class ControllerLogAop {

  val LOG: Logger = LoggerFactory.getLogger(classOf[ControllerLogAop])

  @Autowired
  var objectMapper: ObjectMapper = _

  @Around("execution(* com.bob.scala.webapi.controller.*.*(..))")
  def doAroundMapper(proceedingJoinPoint: ProceedingJoinPoint): Object = {
    val signature: MethodSignature = proceedingJoinPoint.getSignature.asInstanceOf[MethodSignature]
    val method: Method = signature.getMethod
    val className = proceedingJoinPoint.getTarget.getClass.getName

    val start = System.currentTimeMillis()
    LOG.info("executing controller %s method %s request params %s and time is %s".format(className, method.getName,
      objectMapper.writeValueAsString(proceedingJoinPoint.getArgs), start))

    var result: Object = null
    try {
      result = proceedingJoinPoint.proceed
    }
    catch {
      case throwable: Throwable => {
        throw throwable
      }
    }
    val end = System.currentTimeMillis()
    if (LOG.isInfoEnabled) {
      LOG.info("executed controller #{controllername} method #{methodname} response #{response} and total time is #{time} ms"
        .richFormat(
          Map("controllername" -> className,
            "methodname" -> method.getName,
            "response" -> objectMapper.writeValueAsString(method.getReturnType.cast(result)),
            "time" -> (end - start))))
    }
    result
  }
}