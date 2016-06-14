package com.bob.scala.webapi.sptutorial

import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component

/**
 * Created by bob on 16/6/14.
 */
@Component
class InitHelloWorld extends BeanPostProcessor {

  override def postProcessBeforeInitialization(o: scala.Any, s: String): AnyRef = {
    if (s.contains("helloWorld")) {
      println(s"init ${s}")
    }
    o.asInstanceOf[AnyRef]
  }

  override def postProcessAfterInitialization(o: scala.Any, s: String): AnyRef = {
    if (s.contains("helloWorld")) {
      println(s"after ${s}")
    }
    o.asInstanceOf[AnyRef]
  }
}