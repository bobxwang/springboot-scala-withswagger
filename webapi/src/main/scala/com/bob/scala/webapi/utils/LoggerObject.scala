package com.bob.scala.webapi.utils

import org.slf4j.{LoggerFactory, Logger}

/**
 * Created by bob on 16/2/29.
 */
trait LoggerObject {
  /**
   * 返回一个记录日志实例
   * @return
   */
  def LOGGER: Logger = LoggerFactory.getLogger(getClass)
}