package com.bob.java.webapi.service;

import org.springframework.stereotype.Service;

/**
 * Created by bob on 17/3/9.
 */
@Service
public class UrlService {

    public String getApplicationUrl() {
        return "domain.com/myapp";
    }
}