package com.bob.scala.webapi.utils

import scala.reflect.runtime.currentMirror
import scala.tools.reflect.ToolBox

/**
  * Created by wangxiang on 17/9/12.
  */
object CodeInvoke {

  private val toolbox = currentMirror.mkToolBox()

  def invoke(code: String) = {
//    import scala.reflect.runtime.universe._
//    val qcode = q"""$code"""
//    val rs = toolbox.compile(qcode)()

    val rs = toolbox.eval(toolbox.parse(code))
    rs
  }

  def a(): String = {
    val a =
      """
        | import com.bob.scala.webapi.ApplicationContextHolder
        | import com.bob.scala.webapi.service.HelperService
        | val l = ApplicationContextHolder.getBean(classOf[HelperService])
        | val s = l.handlerInput("hello")
        | s
      """.stripMargin
    a
  }
}