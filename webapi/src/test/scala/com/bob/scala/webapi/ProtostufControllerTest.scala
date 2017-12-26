package com.bob.scala.webapi

import com.bob.java.webapi.utils.ProtostufUtils
import com.bob.scala.webapi.controller.{ProtostufController, StubRequest, StubResponse}
import io.protostuff.ProtobufIOUtil
import io.protostuff.runtime.RuntimeSchema
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.{MockMvc, MvcResult, ResultMatcher}

import scala.collection.JavaConverters._

/**
  * Created by bob on 17/2/7.
  */
@RunWith(classOf[SpringRunner])
@WebMvcTest(controllers = Array(classOf[ProtostufController]))
class ProtostufControllerTest {

  @Autowired
  private val mvc: MockMvc = null

  private val template: TestRestTemplate = new TestRestTemplate

  @Test
  def testCheck(): Unit = {
    this.mvc.perform(MockMvcRequestBuilders.get("/protostuf/v1/check"))
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(MockMvcResultMatchers.content().string("OK"))
  }

  @Test
  def testStub(): Unit = {

    val rq = new StubRequest("fucku", 1)
    val byte = ProtostufUtils.serialize(rq)

    this.mvc.perform(MockMvcRequestBuilders
      .post("/protostuf/v1/stub?name=123&age=12")
      .contentType(new MediaType("application", "x-protobuf"))
      .accept(new MediaType("application", "x-protobuf"))
      .content(byte))
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(new ResultMatcher() {
        def `match`(result: MvcResult) {
          result.getResponse.getHeaderNames.asScala.foreach(x => {
            println(s"${x} --> ${result.getResponse.getHeaders(x).asScala.mkString(",")}")
          })

          if (result.getResponse.getContentType == "application/json;charset=UTF-8") {
            println(result.getResponse.getContentAsString)
          } else {
            val byteResult: Array[Byte] = result.getResponse.getContentAsByteArray
            val schema = RuntimeSchema.getSchema(classOf[StubResponse])
            val stubResponse: StubResponse = schema.newMessage()
            ProtobufIOUtil.mergeFrom(byteResult, stubResponse, schema)
            println(stubResponse)
          }
        }
      })
  }

  @Test
  def testListMappers(): Unit = {
    this.mvc.perform(MockMvcRequestBuilders
      .get("/protostuf/v1/mappers"))
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(new ResultMatcher() {
        def `match`(result: MvcResult) {
          val byteResult: Array[Byte] = result.getResponse.getContentAsByteArray
          val schema = RuntimeSchema.getSchema(classOf[String])
          val stubResponse: String = schema.newMessage()
          ProtobufIOUtil.mergeFrom(byteResult, stubResponse, schema)
          println(stubResponse)
        }
      })
  }
}