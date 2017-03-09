package com.bob.scala.webapi.messageq

import java.nio.charset.StandardCharsets

import org.slf4j.{Logger, LoggerFactory}
import org.springframework.amqp.core.Message
import org.springframework.amqp.support.converter.SimpleMessageConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
  * Created by bob on 16/12/24.
  */
@Service
class ObjMsgHandler {

  private val LOGGER: Logger = LoggerFactory.getLogger(classOf[ObjMsgHandler])
  @Autowired private val simpleMessageConverter: SimpleMessageConverter = null

  //  @RabbitListener(queues = Array("ordera"))
  def handlerA(message: Message) {
    val m = message2String(message)
    LOGGER.info(s"aaaa - ${m}")
  }

  def message2String(message: Message): String = {
    val obj = simpleMessageConverter.fromMessage(message)
    if (obj.isInstanceOf[String]) {
      obj.asInstanceOf[String]
    } else if (obj.isInstanceOf[Array[Byte]]) {
      return new String(obj.asInstanceOf[Array[Byte]], StandardCharsets.UTF_8.name())
    }
    return obj.toString
  }
}