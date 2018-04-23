package com.planner.commonUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
}
