package com.bob.scala.webapi

import com.bob.scala.webapi.controller.User
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.{CommandLineRunner, SpringApplication}
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.web.bind.annotation.{RequestMapping, RequestMethod, RestController}
import springfox.documentation.spring.web.json.JsonSerializer

/**
 * Created by bob on 16/2/16.
 */
object ScalaApplication extends App {
  SpringApplication.run(classOf[SampleConfig], args: _ *)
}

@SpringBootApplication
@RestController
class SampleConfig extends CommandLineRunner {

  @Autowired
  val objectMapper: ObjectMapper = null

  @Autowired
  val jsonSerializer: JsonSerializer = null

  @RequestMapping(value = Array("/"), method = Array(RequestMethod.GET))
  def index(): String = {
    "hello, welcome come to scala world"
  }

  override def run(args: String*): Unit = {
    val aUser = new User("c", 4, "a44", 4)
    println(objectMapper.writeValueAsString(aUser))
    println(jsonSerializer.toJson(aUser).value())
  }
}