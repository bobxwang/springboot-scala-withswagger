package com.bob.scala.webapi.config

import org.springframework.amqp.support.converter.SimpleMessageConverter
import org.springframework.context.annotation.{Bean, Configuration, Import}

/**
  * Created by bob on 16/2/27.
  */
@Configuration
@Import(value = Array(classOf[SwaggerConfig], classOf[SpringConfig]))
class WebConfig {

  @Bean
  def simpleObjectMapper(): SimpleMessageConverter = {
    new SimpleMessageConverter
  }
}