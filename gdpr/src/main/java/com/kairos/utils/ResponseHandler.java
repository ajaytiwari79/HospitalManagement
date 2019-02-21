package com.kairos.utils;

import com.kairos.dto.response.ResponseDTO;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseHandler {


    public static ResponseEntity<Object> generateResponse(HttpStatus httpStatus, Boolean isSuccess, Object response) {


        long dateTime = new DateTime(DateTimeZone.UTC).getMillis();

        Map<String, Object> result = new HashMap<>();
        result.put("isSuccess", isSuccess);
        result.put("data", response);
        result.put("status", httpStatus.value());
        result.put("message", httpStatus.value());
        result.put("dateTime", dateTime);

        return new ResponseEntity<>(result, httpStatus);

    }

    public static <T> ResponseEntity<ResponseDTO<T>> generateResponseDTO(HttpStatus status, boolean isSuccess, T responsObj){
        ResponseDTO<T> responseDTO = new ResponseDTO<>(status.value(), isSuccess, responsObj);
        return new ResponseEntity<>(responseDTO, status);
    }

    public static ResponseEntity<Object> invalidResponse(HttpStatus httpStatus, Boolean isSuccess, String message) {


        long dateTime = new DateTime(DateTimeZone.UTC).getMillis();

        Map<String, Object> result = new HashMap<>();
        result.put("isSuccess", isSuccess);
        result.put("message", message);
        result.put("status", httpStatus.value());
        result.put("dateTime", dateTime);

        return new ResponseEntity<>(result, httpStatus);

    }

    ResponseHandler() {

    }


}
