package com.bob.scala.webapi.config

import java.io.IOException
import java.lang.NumberFormatException
import javax.servlet.http.{HttpServletResponse, HttpServletRequest}

import com.bob.scala.webapi.utils.LoggerObject
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.annotation.{Import, Configuration, Lazy}
import org.springframework.hateoas.VndErrors
import org.springframework.http.{ResponseEntity, HttpStatus, HttpHeaders}
import org.springframework.web.bind.annotation.{ExceptionHandler, ResponseBody, RestController, ControllerAdvice}
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.{AbstractJsonpResponseBodyAdvice, ResponseEntityExceptionHandler}

/**
 * Created by bob on 16/2/29.
 */
@Import(value = Array(classOf[RestErrorHandler], classOf[JsonpAdvice]))
class SpringConfig extends LoggerObject {

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

    logError(ex, request);

    val vndErrors: VndErrors = new VndErrors(ex.getClass.getSimpleName, ex.getMessage)
    if (ex.isInstanceOf[NumberFormatException]) {
      response.setStatus(400)
    } else {
      response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
    }

    new ResponseEntity[VndErrors](vndErrors, HttpStatus.valueOf(response.getStatus()))
  }

  /**
   * 重载方法加入日志
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