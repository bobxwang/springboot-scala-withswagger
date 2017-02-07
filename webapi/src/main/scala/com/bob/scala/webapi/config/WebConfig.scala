package com.bob.scala.webapi.config

import java.util

import com.bob.java.webapi.converter.ProtostuffHttpMessageConverter
import com.bob.java.webapi.filter.MDCFilter
import com.bob.java.webapi.handler.ObservableReturnValueHandler
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import org.springframework.amqp.support.converter.SimpleMessageConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.{Bean, Configuration, Import}
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.method.support.HandlerMethodReturnValueHandler
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

/**
  * Created by bob on 16/2/27.
  */
@Configuration
@Import(value = Array(classOf[SwaggerConfig], classOf[SpringConfig]))
class WebConfig extends WebMvcConfigurerAdapter {

  @Autowired
  private val objectMapper: ObjectMapper = null

  @Bean
  def simpleObjectMapper(): SimpleMessageConverter = {
    new SimpleMessageConverter
  }

  @Bean
  def protostuffHttpMessageConverte(): ProtostuffHttpMessageConverter = {
    new ProtostuffHttpMessageConverter()
  }

  @Bean
  def mappingJackson2HttpMessageConverter(): MappingJackson2HttpMessageConverter = {
    val jsonConverter = new MappingJackson2HttpMessageConverter()
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    jsonConverter.setObjectMapper(objectMapper)
    jsonConverter
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