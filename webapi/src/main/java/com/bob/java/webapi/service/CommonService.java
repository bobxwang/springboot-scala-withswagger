package com.bob.java.webapi.service;

import com.bob.java.webapi.constant.MdcConstans;
import com.bob.java.webapi.dto.RxJavaDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.Future;

/**
 * Created by bob on 17/2/8.
 */
@Service
public class CommonService {

    private Logger log = LoggerFactory.getLogger(CommonService.class);

    /**
     * 异步执行,需要返回的Future<>类型
     *
     * @param name
     * @return
     */
    @Async
    public Future<RxJavaDTO> getRxJavaDTO(String name) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("common service begin to process");
        RxJavaDTO item = new RxJavaDTO();
        item.setName(name);
        String value = MDC.get(MdcConstans.MDC_REMOTE_IP);
        if (!StringUtils.isEmpty(value)) {
            log.info("remoteid id " + value);
        } else {
            log.info("remoteid id is empty");
        }
        value = MDC.get(MdcConstans.MDC_ClientRequest_ID);
        if (!StringUtils.isEmpty(value)) {
            log.info("client id " + value);
        } else {
            log.info("client id is empty");
        }
        log.info("common service end to process");
        return new AsyncResult<>(item);
    }
}