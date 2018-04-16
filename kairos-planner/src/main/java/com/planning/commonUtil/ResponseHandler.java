package com.planning.commonUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;

public class ResponseHandler {

	public static Map<String , Object> generateResponse(String message,HttpStatus status, boolean error,Object responseObj){
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map.put("message", message);
			map.put("status", status);
			map.put("error", error);
			map.put("data", responseObj);
			map.put("time_Stamp", new Date());
			return map;
		} catch (Exception e) {
			map.clear();
			map.put("message", e.getMessage());
			map.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
			map.put("time_stamp", new Date());
			return map;
		}
		}
}
