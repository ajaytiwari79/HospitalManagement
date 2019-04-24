package com.kairos.config.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
@Component
public class MyAsyncUncaughtExceptionHandler implements AsyncUncaughtExceptionHandler  {
	private final Logger log = LoggerFactory.getLogger(MyAsyncUncaughtExceptionHandler.class);
	@Override
	public void handleUncaughtException(Throwable arg0, Method arg1, Object... arg2) {
		// TODO Auto-generated method stub
		arg0.printStackTrace();
	}

}
