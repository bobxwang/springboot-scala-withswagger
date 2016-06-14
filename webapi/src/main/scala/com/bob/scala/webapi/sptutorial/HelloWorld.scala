package com.bob.scala.webapi.sptutorial

import javax.annotation.{PostConstruct, PreDestroy}

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import scala.beans.BeanProperty

/**
 * Created by bob on 16/6/14.
 */
@Component
class HelloWorld {

  @PostConstruct
  def init() {
    println("HelloWorld Bean is going through init.")
  }

  @PreDestroy
  def destory() {
    println("HelloWorld Bean is going through destroy.")
  }

  @BeanProperty
  @Value("${helloworld.message}")
  var message: String = _

  /**
   * Spring Bean definition inheritance has nothing to do with Java class inheritance but inheritance concept is same.
   * You can define a parent bean definition as a template and other child beans can inherit required configuration from the parent bean.
   */

}