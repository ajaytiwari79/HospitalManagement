package com.kairos.scheduler.utils;

import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.response.ResponseDTO;
import org.json.JSONObject;
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


        Map<String, Object> map = new HashMap<String, Object>(4);
        map.put("status", status.value());
        map.put("isSuccess", isSuccess);
        map.put("data", responseObj);
        map.put("time_stamp", DateUtils.getCurrentDate());
        return new ResponseEntity<Map<String, Object>>(map, status);

    }

    public static <T> ResponseEntity<ResponseDTO<T>> generateResponseDTO(HttpStatus status, boolean isSuccess, T responsObj){
        ResponseDTO<T> responseDTO = new ResponseDTO<T>(status.value(), isSuccess, responsObj);
        return new ResponseEntity<>(responseDTO, status);
    }

    public static ResponseEntity<String> generateResponse(JSONObject jsonObject) {
        return new ResponseEntity<String>(jsonObject.toString(), HttpStatus.UNAUTHORIZED);
    }


}
