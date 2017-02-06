package com.bob.scala.webapi.service

import com.bob.java.webapi.constant.MdcConstans
import org.slf4j.{Logger, LoggerFactory, MDC}
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils

/**
  * Created by bob on 17/2/6.
  */
@Service
class HelperService {

  private val LOGGER: Logger = LoggerFactory.getLogger(classOf[HelperService])

  def handlerInput(param: String): String = {
    LOGGER.info(s"handlerInputParm/${param} begin to process")
    Thread.sleep(1000 * 2)
    val sb: StringBuilder = new StringBuilder(param)
    var value: String = MDC.get(MdcConstans.MDC_REMOTE_IP)
    if (!StringUtils.isEmpty(value)) sb.append("\r remoteip is " + value)
    value = MDC.get(MdcConstans.MDC_ClientRequest_ID)
    if (!StringUtils.isEmpty(value)) sb.append("\r clientid is " + value)
    LOGGER.info(s"handlerInputParm/${param} stop to process")
    sb.toString
  }
}