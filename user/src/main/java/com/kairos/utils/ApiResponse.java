package com.kairos.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Created by prabjot on 22/11/16.
 */
public class ApiResponse {

    String message;
    private HttpStatus httpStatus;
    private boolean isSuccess;
    long timeStamp;
    private Object data;
    int status;

    public ResponseEntity<ApiResponse> send(String message,HttpStatus httpStatus,boolean isSuccess,Object data) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.status = httpStatus.value();
        this.isSuccess = isSuccess;
        this.timeStamp = DateUtil.getCurrentDate().getTime();
        this.data = data;
        return new ResponseEntity<ApiResponse>(this, httpStatus);
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public Object getData() {
        return data;
    }

    public int getStatus() {
        return status;
    }
}
