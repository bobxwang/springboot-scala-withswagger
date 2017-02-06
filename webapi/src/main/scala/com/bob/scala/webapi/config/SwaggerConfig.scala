package com.bob.scala.webapi.config

import javax.annotation.PostConstruct

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.json.PackageVersion
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.{JsonSerializer, ObjectMapper, SerializerProvider}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.{Bean, Scope}
import org.springframework.web.context.request.async.DeferredResult
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors.regex
import springfox.documentation.schema.configuration.ObjectMapperConfigured
import springfox.documentation.service.ApiInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

/**
  * Created by bob on 16/2/27.
  */
@EnableSwagger2
class SwaggerConfig {

  @Value("${swagger.ui.enable}")
  val swagger_ui_enable: Boolean = false

  @Value("${swagger.ui.enable}")
  var swagger_ui_enable_string: String = _

  //  @Autowired
  //  val objectMapper: ObjectMapper = null

  /**
    * 用户接口列表
    *
    * @return
    */
  @Bean
  @Scope("singletion")
  def userDocketFactory: Docket = {
    val docket = new Docket(DocumentationType.SWAGGER_2)
      .apiInfo(apiInfo)
      .groupName("userInterface")
      .select()
      .paths(regex("/users.*"))
      .build()
      .useDefaultResponseMessages(false)
    docket.enable(swagger_ui_enable)
  }

  def apiInfo: ApiInfo = {
    new ApiInfoBuilder()
      .title("Spring Boot Using Scala With Swagger")
      .description("User Interface API")
      .contact("wangxiang@u51.com")
      .license("Apache License, Version 2.0")
      .version("1.0")
      .build()
  }

  @PostConstruct
  def initObject(): Unit = {
    //    objectMapper.registerModule(DefaultScalaModule)
  }

  /**
    * 如果采用注入objectMapper，然后在initObject中添加对scala的处理，那样在帮助文档中至少目前不会显示@ApiModelProperty的注解，
    * 采用事件触发添加会显示，但测试下来发现只显示Response的，如果PostBody则又会忽略，感觉这块跟scala结合的还不是很好，
    * 也有可能跟scala的类定义有关，还需仔细看下
    *
    * @return
    */
  @Bean
  def objectMapperInitializer = new ApplicationListener[ObjectMapperConfigured] {
    def onApplicationEvent(event: ObjectMapperConfigured) = {
      event.getObjectMapper.registerModule(DefaultScalaModule)

      val newModule = new SimpleModule("UTCDateDeserializer", PackageVersion.VERSION)
      newModule.addSerializer(classOf[DeferredResult[_]], new DeferredResultSerializer(event.getObjectMapper))
      event.getObjectMapper.registerModule(newModule)
    }
  }
}

class DeferredResultSerializer(objectMapper: ObjectMapper) extends JsonSerializer[DeferredResult[_]] {

  override def serialize(value: DeferredResult[_], gen: JsonGenerator, serializers: SerializerProvider) = {
    gen.writeString(objectMapper.writeValueAsString(value.getResult))
  }
}