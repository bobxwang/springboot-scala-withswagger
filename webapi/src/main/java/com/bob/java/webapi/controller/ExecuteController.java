package com.bob.java.webapi.controller;

import com.bob.scala.webapi.utils.CodeInvoke;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

/**
 * Created by wangxiang on 17/9/12.
 */
@Controller
public class ExecuteController {

    @GetMapping("/scalaexecute")
    public String gScalaExecute() {
        return "scalaexecute";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/scalaexecute")
    @ResponseBody
    public Map<String, String> pScalaExecute(@RequestBody Map<String, String> jsonNodes) {
        Map<String, String> map = Maps.newHashMap();
        String rs = jsonNodes.get("content");
        map.put("rs", CodeInvoke.invoke(rs).toString());
        map.put("now", new Date().toString());
        return map;
    }
}