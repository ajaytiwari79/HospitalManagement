package com.planning.commonUtil;

public class OptaNotFoundException extends RuntimeException{
	  public OptaNotFoundException(String id) {
	        super(String.format("No todo entry found with id: <%s>", id));
	    }

}
