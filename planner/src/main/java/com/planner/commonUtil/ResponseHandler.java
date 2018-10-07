package com.planner.commonUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.kairos.dto.response.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseHandler {

	public static ResponseEntity<Map<String, Object>> generateResponse(String message, HttpStatus status){
		Map<String, Object> map = new HashMap<String, Object>();
			map.put("message", message);
			map.put("status",status.toString());
			map.put("time_Stamp", new Date());
			return new ResponseEntity<>(map,status);
		}

	public static <T> ResponseEntity<ResponseDTO<T>> generateResponseDTO(HttpStatus status, boolean isSuccess, T responsObj){
		ResponseDTO<T> responseDTO = new ResponseDTO<T>(status.value(), isSuccess, responsObj);
		return new ResponseEntity<>(responseDTO, status);
	}

	public static ResponseEntity<Map<String, Object>> generateResponseWithData(String message, HttpStatus status,Object object){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("message", message);
		map.put("status",status.toString());
		map.put("time_Stamp", new Date());
		map.put("data",object);
		return new ResponseEntity<>(map,status);
	}
}
