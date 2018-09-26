package com.kairos.dto.response;

import com.kairos.commons.utils.DateUtils;

public class ResponseDTO<T> {
    private int status;
    private boolean success;
    private T data;
    private long time_stamp = DateUtils.getCurrentMillis();

    public ResponseDTO() {
    }

    public ResponseDTO(int status, boolean success, T data){
        this.status = status;
        this.success = success;
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public long getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(long time_stamp) {
        this.time_stamp = time_stamp;
    }
}
