package com.kairos.commons.utils;

import com.kairos.dto.response.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by prabjot on 9/20/16.
 */
public final class ResponseHandler {

    private ResponseHandler() {
    }

    public static ResponseEntity<Map<String, Object>> generateResponse(HttpStatus status, boolean isSuccess, Object responseObj) {
        // Get Time as per UTC format
        long dateTime = DateUtils.getCurrentMillis();
        Map<String, Object> map = new HashMap<String, Object>(4);
        map.put("status", status.value());
        map.put("isSuccess", isSuccess);
        map.put("data", responseObj);
        map.put("time_stamp", dateTime);
        return new ResponseEntity<Map<String, Object>>(map, status);
    }

    public static <T> ResponseEntity<ResponseDTO<T>> generateResponseDTO(HttpStatus status, boolean isSuccess, T responsObj){
        ResponseDTO<T> responseDTO = new ResponseDTO<T>(status.value(), isSuccess, responsObj);
        return new ResponseEntity<>(responseDTO, status);
    }

    public static ResponseEntity<Map<String, Object>> invalidResponse(HttpStatus status, boolean isSuccess, Object errors) {
        // Get Time as per UTC format
        long dateTime = DateUtils.getCurrentMillis();

        Map<String, Object> map = new HashMap<String, Object>(4);
        map.put("status", status.value());
        map.put("isSuccess", isSuccess);
        map.put("errors", errors);
        map.put("message", status.name());
        map.put("time_stamp", dateTime);
        return new ResponseEntity<Map<String, Object>>(map, status);

    }

}
