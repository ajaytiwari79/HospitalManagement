package com.kairos.commons.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by prabjot on 24/8/17.
 */
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
