package com.planner.commonUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;

public class ResponseHandler {

	public static Map<String , Object> generateResponse(String message,HttpStatus status){
		Map<String, Object> map = new HashMap<String, Object>();
			map.put("message", message);
			map.put("status", status);
			map.put("time_Stamp", new Date());
			return map;
		}
}
