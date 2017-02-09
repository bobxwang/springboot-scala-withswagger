package com.bob.scala.webapi.config

import java.util

import com.bob.java.webapi.converter.ProtostuffHttpMessageConverter
import com.bob.java.webapi.filter.MDCFilter
import com.bob.java.webapi.handler.ObservableReturnValueHandler
import com.bob.java.webapi.spextension.{MDCCallableProcessingInterceptor, MDCDeferredResultProcessingInterceptor, MDCSimpleAsyncTaskExecutor}
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import org.springframework.amqp.support.converter.SimpleMessageConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.{Bean, Configuration, Import}
import org.springframework.core.task.SimpleAsyncTaskExecutor
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.method.support.HandlerMethodReturnValueHandler
import org.springframework.web.servlet.config.annotation.{AsyncSupportConfigurer, WebMvcConfigurerAdapter}

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

  @Bean def simpleAsyncTaskExecutor: SimpleAsyncTaskExecutor = new MDCSimpleAsyncTaskExecutor

  override def configureAsyncSupport(configurer: AsyncSupportConfigurer) = {
    configurer.setTaskExecutor(simpleAsyncTaskExecutor)
    configurer.setDefaultTimeout(4000L)
    //        configurer.registerCallableInterceptors(mdcCallableProcessingInterceptor())
    configurer.registerDeferredResultInterceptors(mdcDeferredResultProcessingInterceptor())
  }

  @Bean
  def mdcCallableProcessingInterceptor(): MDCCallableProcessingInterceptor = {
    new MDCCallableProcessingInterceptor()
  }

  @Bean
  def mdcDeferredResultProcessingInterceptor(): MDCDeferredResultProcessingInterceptor = {
    new MDCDeferredResultProcessingInterceptor()
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