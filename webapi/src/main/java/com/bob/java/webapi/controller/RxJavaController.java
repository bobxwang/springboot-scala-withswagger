package com.bob.java.webapi.controller;

import com.bob.java.webapi.dto.RxJavaDTO;
import com.bob.java.webapi.service.RxJavaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import rx.Observable;

/**
 * 异步例子,返回RxJava的可观察容器
 * Created by bob on 17/2/4.
 */
@RestController
@RequestMapping(value = "rxjava/v1")
public class RxJavaController {

    private static final Logger log = LoggerFactory.getLogger(RxJavaController.class);

    @Autowired
    private RxJavaService rxJavaService;

    @RequestMapping(value = "/rx/observable/{name}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public Observable<RxJavaDTO> observable(
            @PathVariable("name") String name) {
        log.info("rs/observable/ begin to process");
        Observable<RxJavaDTO> temp = rxJavaService.compose(name);
        log.info("rs/observable/ stop to process");
        return temp;
    }

    @RequestMapping(value = "/rx/noobservable/{name}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public RxJavaDTO noobservable(@PathVariable("name") String name) {
        log.info("rs/noobservable/ begin to process");
        RxJavaDTO temp = rxJavaService.getRxJavaDTO(name);
        log.info("rs/noobservable/ stop to process");
        return temp;
    }
}