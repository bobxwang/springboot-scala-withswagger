package com.bob.scala.webapi.config

import java.util

import com.bob.java.webapi.filter.MDCFilter
import com.bob.java.webapi.handler.ObservableReturnValueHandler
import org.springframework.amqp.support.converter.SimpleMessageConverter
import org.springframework.context.annotation.{Bean, Configuration, Import}
import org.springframework.web.method.support.HandlerMethodReturnValueHandler
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

/**
  * Created by bob on 16/2/27.
  */
@Configuration
@Import(value = Array(classOf[SwaggerConfig], classOf[SpringConfig]))
class WebConfig extends WebMvcConfigurerAdapter {

  @Bean
  def simpleObjectMapper(): SimpleMessageConverter = {
    new SimpleMessageConverter
  }

  @Bean
  def mdcFilter(): MDCFilter = {
    new MDCFilter
  }

  override def addReturnValueHandlers(returnValueHandlers: util.List[HandlerMethodReturnValueHandler]) = {
    super.addReturnValueHandlers(returnValueHandlers)
    returnValueHandlers.add(new ObservableReturnValueHandler)
  }
}