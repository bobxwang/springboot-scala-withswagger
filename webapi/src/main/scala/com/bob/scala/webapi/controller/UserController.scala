package com.bob.scala.webapi.controller

import javax.validation.Valid
import javax.validation.constraints.{Max, Min, NotNull}

import com.bob.scala.webapi.exception.{ServerException, ClientException}
import io.swagger.annotations._
import org.springframework.hateoas.VndErrors
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation._

import scala.collection.JavaConverters._
import scala.concurrent.{Promise, Future}

@ApiModel("用户基本属性")
case class User(@ApiModelProperty("姓名") name: String,
                @ApiModelProperty("年纪") age: Int,
                @ApiModelProperty("地址") address: String,
                @ApiModelProperty("性别") sex: Int)

@ApiModel("添加用户基本信息参数")
case class AddUserParam() {
  @ApiModelProperty("姓名")
  @NotNull
  var name: String = _;
  @ApiModelProperty("年纪")
  @Min(value = 1l, message = "年纪最少不能少于1")
  @Max(value = 100l, message = "年纪最大不能大于100")
  var age: Int = _;
  @ApiModelProperty("地址")
  @NotNull
  var address: String = _;
  @ApiModelProperty("性别")
  var sex: Int = _;
}

/**
 * Created by bob on 16/2/27.
 */
@RestController
@RequestMapping(value = Array("users/v1"))
@Api(value = "用户相关接口", description = "用户相关接口")
class UserController {

  @RequestMapping(value = Array("lists"), method = Array(RequestMethod.GET))
  @ApiOperation(value = "列出系统中所有用户信息")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(Array(
    new ApiResponse(code = 401, message = "无权限操作", response = classOf[VndErrors]),
    new ApiResponse(code = 404, message = "没有处理器", response = classOf[VndErrors]),
    new ApiResponse(code = 204, message = "记录不存在", response = classOf[VndErrors])))
  def lists(): java.util.List[User] = {
    val aUser = new User("c", 4, "a44", 4)
    val aList = List(new User("a", 1, "a11", 1), new User("b", 2, "b22", 2), new User("c", 3, "c33", 3))
    aList.+:(aUser).asJava
  }

  /**
   * spring中全局出错处理器其实是由java触发的，那么此时需要通过注解来告诉会有哪些异常，
   * 要不然所有的ex类型都会成为UndeclaredThrowableException，就不能采用模式匹配
   */
  @throws(classOf[ClientException])
  @throws(classOf[ServerException])
  @RequestMapping(value = Array("lists/{name}"), method = Array(RequestMethod.GET))
  @ApiOperation("根据姓名查找用户")
  def findByName(@PathVariable("name") @ApiParam("用户姓名") name: String): User = {
    if (name == "abcde") {
      throw new ClientException("参数非法")
    }
    if (name == "abcd") {
      throw new IllegalArgumentException("参数出错")
    }
    User(name, 4, "a44", 4)
  }

  @RequestMapping(value = Array("lists"), method = Array(RequestMethod.POST))
  @ApiOperation("创建一个用户")
  @throws(classOf[ServerException])
  def createUser(@Valid @RequestBody param: AddUserParam): User = {
    if (param.name == "fuck") {
      throw ServerException(s"server error,${param.name}")
    }
    User(param.name, param.age, param.address,
      param.age)
  }
}