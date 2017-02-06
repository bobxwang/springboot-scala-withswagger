package com.bob.java.webapi.service;

import com.bob.java.webapi.dto.RxJavaDTO;
import com.bob.scala.webapi.service.HelperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.Observable;

/**
 * Created by bob on 17/2/4.
 */
@Service
public class RxJavaService {

    private Logger log = LoggerFactory.getLogger(RxJavaService.class);

    @Autowired
    private HelperService helperService;

    public Observable<RxJavaDTO> compose(String name) {
        return Observable.<RxJavaDTO>create(sub -> {
            RxJavaDTO item = getRxJavaDTO(name);
            sub.onNext(item);
            sub.onCompleted();
        });
    }

    public RxJavaDTO getRxJavaDTO(String name) {
        RxJavaDTO item = new RxJavaDTO();
        item.setName(helperService.handlerInput(name));
        return item;
    }
}