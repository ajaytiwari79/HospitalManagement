package com.kairos.activity.response.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kairos.activity.controller.exception_handler.FieldErrorDTO;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

@JsonInclude(value=Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseEnvelope {
	// Get Time as per UTC format
	final Long time_stamp = new DateTime( DateTimeZone.UTC).getMillis();

	private  boolean success;
	private Object data;
	private String path;
	private String message;
	private List<FieldErrorDTO> errors;

	public ResponseEnvelope() {}

	public ResponseEnvelope(Object body,String message,String path) {
		this.success=true;
		this.data=body;
		this.message=message;
		this.path=path;
	}

	public void addError(FieldErrorDTO error) {
		if(errors==null)
		{
			errors = new ArrayList< FieldErrorDTO >();
		}
		errors.add(error);
	}
	@JsonProperty("isSuccess")
	public boolean isSuccess() {
		return success;
	}

	@JsonProperty("success")
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public List<FieldErrorDTO> getErrors() {
		return errors;
	}

	public void setErrors(List<FieldErrorDTO> errors) {
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

	

}
