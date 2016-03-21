package com.bob.scala.webapi.exception

/**
 * Created by bob on 16/3/2.
 */
case class ClientException(errMessage: String, status: Int = 400) extends Exception(errMessage) {
  def this() {
    this("", 400)
  }
}

case class ServerException(errMessage: String, status: Int = 500) extends Exception(errMessage) {
  def this() {
    this("", 500)
  }
}