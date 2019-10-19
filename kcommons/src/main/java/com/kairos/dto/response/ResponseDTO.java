package com.kairos.dto.response;

import com.kairos.commons.utils.DateUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResponseDTO<T> {
    private int status;
    private boolean success;
    private T data;
    private long time_stamp = DateUtils.getCurrentMillis();

    public ResponseDTO(int status, boolean success, T data){
        this.status = status;
        this.success = success;
        this.data = data;
    }

}
