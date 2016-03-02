package com.bob.scala.webapi.exception

/**
 * Created by bob on 16/3/2.
 */
case class ClientException(var errMessage: String, var status: Int = 400) extends Exception(errMessage) {
  def this() {
    this("", 400)
  }
}

case class ServerException(var errMessage: String, var status: Int = 500) extends Exception(errMessage) {
  def this() {
    this("", 500)
  }
}