package com.bob.scala.webapi.config

import org.springframework.context.annotation.{Import, Configuration}

/**
 * Created by bob on 16/2/27.
 */
@Configuration
@Import(value = Array(classOf[SwaggerConfig]))
class WebConfig {

}