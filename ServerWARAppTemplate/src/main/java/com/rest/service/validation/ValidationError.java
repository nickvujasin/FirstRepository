package com.rest.service.validation;

public class ValidationError {

	private String field;
	private String message;
	
	public ValidationError(String message) {
		this.field = "General";
		this.message = message;
	}
	
	public ValidationError(String field, String message) {
		this.field = field;
		this.message = message;
	}
	
	public String getField() {
		return field;
	}
	
	public String getMessage() {
		return message;
	}
	
	@Override
	public String toString() {
		return new StringBuilder().append(field).append(":").append(message).toString();
	}
}
