package com.bob.java.webapi.dto;

/**
 * Created by bob on 17/2/4.
 */
public class RxJavaDTO {

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    public RxJavaDTO() {

    }

    public RxJavaDTO(String name) {
        this.name = name;
    }
}