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
  /**
   * args: _ *:此标注告诉编译器把args中的每个元素当作参数，而不是当作一个当一的参数传递
   */
  SpringApplication.run(classOf[SampleConfig], args: _ *)
}

@SpringBootApplication
@RestController
class SampleConfig extends CommandLineRunner {

  @Autowired
  var objectMapper: ObjectMapper = _

  /**
   * 只有使用swagger的基础上才能导入此实例
   */
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
    val map = Map("message" -> "fucktest")
    println(objectMapper.writeValueAsString(map))
  }
}