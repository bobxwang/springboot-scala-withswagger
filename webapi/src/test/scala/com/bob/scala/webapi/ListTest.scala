package com.bob.scala.webapi

import java.io.{File, FilenameFilter}
import java.util
import java.util.Date

import org.junit.Test

/**
  * Created by bob on 17/2/10.
  */
class ListTest {

  case class User(userid: Long, createat: Date, id: Long)

  @Test
  def testStub(): Unit = {
    val f = new File("/Users/bob/Works/enniu/JProject/fc-risk-dataapi")
    if (!f.isDirectory) {
      return
    }
    val fd = depthScaner(f, "java")
    import collection.JavaConverters._
    fd.asScala.foreach(println)
    println("*****-----" * 5)
    val fw = widthScaner(f, "java")
    fw.asScala.foreach(println)
  }

  case class BBFileFilter(suffix: String) extends FilenameFilter {
    def accept(dir: File, name: String): Boolean = {
      val f = new File(dir, name)
      if (f.isFile) {
        return f.getName.endsWith(suffix)
      }
      return true
    }
  }

  /**
    * 深度搜索
    *
    * @param path
    * @param suffix
    */
  def depthScaner(path: File, suffix: String): util.ArrayList[String] = {
    val list = new util.ArrayList[String]()
    val fs = path.listFiles(BBFileFilter(suffix))
    for (f: File <- fs) {
      if (f.isDirectory) {
        list.addAll(depthScaner(f, suffix))
      } else {
        list.add(f.getAbsolutePath)
      }
    }
    list
  }

  /**
    * 广度搜索
    *
    * @param path
    * @param suffix
    */
  def widthScaner(path: File, suffix: String): util.ArrayList[String] = {
    val list = new util.ArrayList[String]()

    val queue = new util.ArrayDeque[File]()
    queue.offer(path)
    val filter = BBFileFilter(suffix)

    while (!queue.isEmpty) {
      val p = queue.poll()
      val fs = p.listFiles(filter)
      for (f <- fs) {
        if (f.isFile) {
          list.add(f.getAbsolutePath)
        } else {
          queue.offer(f)
        }
      }
    }

    list
  }
}