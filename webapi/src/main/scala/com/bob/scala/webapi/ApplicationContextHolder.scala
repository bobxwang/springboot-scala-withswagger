package com.bob.scala.webapi

import java.util
import java.util.{Objects}

import org.springframework.beans.BeansException
import org.springframework.beans.factory.DisposableBean
import org.springframework.context.{ApplicationContext, ApplicationContextAware}

/**
  * Created by wangxiang on 17/9/12.
  */
object ApplicationContextHolder {

  def getAppCtx: ApplicationContext = appCtx

  private var appCtx: ApplicationContext = null

  private def cleanApplicationContext(): Unit = {
    appCtx = null
  }

  @SuppressWarnings(Array("unchecked"))
  @throws[BeansException]
  def getBean[T](name: String): T = {
    checkApplicationContext()
    appCtx.getBean(name).asInstanceOf[T]
  }

  @SuppressWarnings(Array("unchecked"))
  @throws[BeansException]
  def getBean[T](clazz: Class[T]): T = {
    checkApplicationContext()
    appCtx.getBean(clazz)
  }

  @SuppressWarnings(Array("unchecked"))
  @throws[BeansException]
  def getBeansOfType[T](clazz: Class[T]): util.Map[String, T] = {
    checkApplicationContext()
    appCtx.getBeansOfType(clazz)
  }

  private def checkApplicationContext(): Unit = {
    if (appCtx == null) throw new IllegalStateException("applicaitonContext未注入,请在applicationContext.xml中定义SpringContextHolder")
  }
}

final class ApplicationContextHolder extends ApplicationContextAware with DisposableBean {
  @throws[Exception]
  override def destroy(): Unit = {
    ApplicationContextHolder.cleanApplicationContext()
  }

  override def setApplicationContext(appCtx: ApplicationContext): Unit = {
    Objects.requireNonNull(appCtx, "Application Context is required")
    ApplicationContextHolder.appCtx = appCtx
  }
}