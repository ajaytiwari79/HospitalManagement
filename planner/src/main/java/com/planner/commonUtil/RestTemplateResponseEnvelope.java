package com.planner.commonUtil;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RestTemplateResponseEnvelope<T> {

    private T data;
    private String message;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "RestTemplateResponseEnvelope{" +
                "data=" + data +
                ", message='" + message + '\'' +
                '}';
    }
}

