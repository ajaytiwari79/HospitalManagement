package com.kairos.utils;

public class FilterResponseWithData<T> {


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
        return "filter response {" +
                "data=" + data +
                ", message='" + message + '\'' +
                '}';
    }

}
