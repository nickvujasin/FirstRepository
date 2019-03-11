package com.rest.service.validation;

import java.util.ArrayList;
import java.util.List;

public class ValidationException extends Exception {

	private static final long serialVersionUID = -7578968929434838942L;
	
	private List<ValidationError> errors;

	public ValidationException(String message, List<ValidationError> errors) {
		super(message);
		this.errors = errors;
	}

	public ValidationException(String message) {
		super(message);
		this.errors = new ArrayList<ValidationError>();
		this.errors.add(new ValidationError("General", message));
	}
	
	public List<ValidationError> getValidationErrors() {
		return errors;
	}
}
