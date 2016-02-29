package com.bob.scala.webapi.utils

/**
 * Created by bob on 16/2/29.
 */
object StringImplicit {

  /**
   * 格式化一个字符串，传入Key-Value，字符串中相应的key会被value替换
   *
   * @param string
   * @return
   */
  implicit def RichFormatter(string: String) = new {
    def richFormat(replacement: Map[String, Any]) = {
      (string /: replacement) { (res, entry) => res.replaceAll("#\\{%s\\}".format(entry._1), entry._2.toString) }
    }
  }
}