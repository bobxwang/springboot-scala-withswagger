package com.bob.java.webapi.dto;

/**
 * Created by bob on 17/3/9.
 */
public class Form {
    private String name = "spring.io";
    private String url = "http://spring.io";
    private String tags = "#spring #framework #java";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}