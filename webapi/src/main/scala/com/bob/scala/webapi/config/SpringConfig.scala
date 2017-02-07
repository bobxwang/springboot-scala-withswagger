package com.bob.scala.webapi.config

import java.io.IOException
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import com.bob.scala.webapi.exception.{ClientException, ServerException}
import com.bob.scala.webapi.utils.LoggerObject
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.hateoas.VndErrors
import org.springframework.http.{HttpHeaders, HttpStatus, ResponseEntity}
import org.springframework.web.bind.annotation.{ControllerAdvice, ExceptionHandler, ResponseBody, RestController}
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.{AbstractJsonpResponseBodyAdvice, ResponseEntityExceptionHandler}

/**
  * Created by bob on 16/2/29.
  */
@Import(value = Array(classOf[RestErrorHandler], classOf[JsonpAdvice]))
class SpringConfig {
}

@ControllerAdvice(annotations = Array(classOf[RestController]))
class JsonpAdvice extends AbstractJsonpResponseBodyAdvice("callback", "jsonp") {

}

/**
  * 全局统一出错处理
  */
@ControllerAdvice(annotations = Array(classOf[RestController]))
class RestErrorHandler extends ResponseEntityExceptionHandler with LoggerObject {

  @Autowired
  var objectMapper: ObjectMapper = null

  @ExceptionHandler(value = Array(classOf[Exception]))
  @ResponseBody
  @throws(classOf[IOException])
  def handleRequests(request: HttpServletRequest, response: HttpServletResponse,
                     ex: Exception): ResponseEntity[VndErrors] = {

    logError(ex, request)

    val message = if (ex.getMessage == null) "null" else ex.getMessage
    val vndErrors: VndErrors = new VndErrors(ex.getClass.getSimpleName, message)
    ex match {
      case ClientException(err, state) => response.setStatus(state)
      case ServerException(err, state) => response.setStatus(state)
      case _ => response.setStatus(500)
    }

    new ResponseEntity[VndErrors](vndErrors, HttpStatus.valueOf(response.getStatus()))
  }

  /**
    * 重载方法加入日志
    *
    * @param ex
    * @param body
    * @param headers
    * @param status
    * @param request
    * @return
    */
  override def handleExceptionInternal(ex: Exception, body: scala.Any,
                                       headers: HttpHeaders, status: HttpStatus,
                                       request: WebRequest): ResponseEntity[AnyRef] = {
    logError(ex)
    super.handleExceptionInternal(ex, body, headers, status, request)
  }

  def logError(ex: Exception): Unit = {
    try {
      val map = Map("message" -> ex.getMessage)
      LOGGER.error(objectMapper.writeValueAsString(map), ex)
    } catch {
      case e: Exception => {}
    }
  }

  def logError(ex: Exception, request: HttpServletRequest): Unit = {
    try {
      val map = Map("message" -> ex.getMessage,
        "from" -> request.getRemoteAddr,
        "path" -> (if (request.getQueryString == null) request.getRequestURI else request.getRequestURI + "?" + request.getQueryString));
      LOGGER.error(objectMapper.writeValueAsString(map), ex)
    } catch {
      case e: Exception => {}
    }
  }
}