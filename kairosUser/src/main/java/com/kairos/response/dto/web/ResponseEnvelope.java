package com.kairos.response.dto.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.List;
//TODO move to commons, sachin
@JsonInclude(value=Include.NON_NULL)
public class ResponseEnvelope {
	// Get Time as per UTC format
	final Long time_stamp = new DateTime( DateTimeZone.UTC).getMillis();

	private  boolean success;
	private Object data;
	private String path;
	private String message;
	private List<com.kairos.controller.exception_handler.FieldErrorDTO> errors;


	public ResponseEnvelope() {}

	public ResponseEnvelope(Object body, String message, String path) {
		this.success=true;
		this.data=body;
		this.message=message;
		this.path=path;
	}

	public void addError(com.kairos.controller.exception_handler.FieldErrorDTO error) {
		if(errors==null)
		{
			errors = new ArrayList<com.kairos.controller.exception_handler.FieldErrorDTO>();
		}
		errors.add(error);
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
	public List<com.kairos.controller.exception_handler.FieldErrorDTO> getErrors() {
		return errors;
	}

	public void setErrors(List<com.kairos.controller.exception_handler.FieldErrorDTO> errors) {
		this.errors = errors;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Long getTime_stamp() {
		return time_stamp;
	}
}
