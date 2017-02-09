package com.bob.java.webapi.controller;

import com.bob.java.webapi.service.CommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by bob on 17/2/8.
 */
@RestController
@RequestMapping(value = "common/v1")
public class CommonController {

    private static final Logger log = LoggerFactory.getLogger(CommonController.class);

    @Autowired
    private CommonService commonService;

    @RequestMapping(value = "chandle/{name}", method = RequestMethod.GET)
    public String chandle(
            @PathVariable("name") String name) {
        log.info("rs/chandle/ begin to process");
        String temp = name.concat("-other-name-").concat(name);
        log.info("rs/chandle/ stop to process");
        return temp;
    }

    @RequestMapping(value = "asynchandle/{name}", method = RequestMethod.GET)
    public String asynchandle(
            @PathVariable("name") String name) {
        log.info("rs/asynchandle/ begin to process");
        commonService.getRxJavaDTO(name);
        log.info("rs/asynchandle/ stop to process");
        return name;
    }
}