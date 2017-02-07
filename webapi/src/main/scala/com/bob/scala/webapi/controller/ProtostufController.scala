package com.bob.scala.webapi.controller

import javax.servlet.http.HttpServletRequest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.{RequestBody, RequestMapping, RequestMethod, RestController}
import org.springframework.web.servlet.mvc.method.annotation.{RequestMappingHandlerAdapter, RequestMappingHandlerMapping}

import scala.collection.JavaConverters._

case class StubResponse(name: String, age: Int)

class StubRequest(var name: String, var age: Int) {

  def this() {
    this("", 0)
  }
}

/**
  * Created by bob on 17/2/7.
  */
@RestController
@RequestMapping(value = Array("protostuf/v1"))
class ProtostufController {

  @Autowired
  private val requestMappingHandlerMapping: RequestMappingHandlerMapping = null

  @Autowired
  private val requestMappingHandlerAdapter: RequestMappingHandlerAdapter = null

  @RequestMapping(value = Array("/check"), method = Array(RequestMethod.GET))
  def check() = {
    "OK"
  }

  /**
    * consumes属性限制请求头中的Content-Type值是application/x-protobuf才会处理
    * produces告诉客户端经过此处理后会返回类型是application/x-protobuf的数据,让其在Accept中指定此值
    *
    * @return
    */
  @RequestMapping(value = Array("/stub"),
    method = Array(RequestMethod.POST),
    consumes = Array("application/x-protobuf"),
    produces = Array("application/x-protobuf")
  )
  def stub(@RequestBody rq: StubRequest, request: HttpServletRequest): StubResponse = {
    println(s"age --> ${request.getParameter("ageage")}")
    println(s"name --> ${request.getParameter("name")}")
    new StubResponse(rq.name + "ab" + rq.name.length, rq.age + rq.name.length)
  }

  @RequestMapping(value = Array("/actions"), method = Array(RequestMethod.GET))
  def listActions(): String = {
    val sb = new StringBuilder
    sb.append("URL").append("--").append("Class").append("--").append("Function").append('\n')
    requestMappingHandlerMapping.getHandlerMethods().asScala.foreach(x => {
      sb.append(x._1).append("--")
        .append(x._2.getMethod.getDeclaringClass).append("--")
        .append(x._2.getMethod.getName).append("\n")
    })

    sb.toString()
  }

  @RequestMapping(value = Array("/mappers"),
    method = Array(RequestMethod.GET))
  def listMappers(): String = {
    val sb = new StringBuilder
    sb.append("URL").append('\n')
    requestMappingHandlerAdapter.getMessageConverters.asScala.foreach(x => {
      sb.append(x.getClass.getSimpleName).append("\n")
    })

    sb.toString()
  }
}