package com.rest.service.validation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.rest.domain.BaseDomain;

@Component
public class DomainValidatorFactory {

	// Spring finds all the Validators that implement SmartValidator and auto populates this list.
	@Autowired
	private List<Validator> validatorList;

	public void validateDomain(BaseDomain domain, Object... hints) throws ValidationException {
		
		BeanPropertyBindingResult errors = new BeanPropertyBindingResult(domain, "Errors");

		if (domain == null) {
			throw new ValidationException("Domain Validation Failed - domain object is null.");
		} else {
			// Iterate over all the validators and find the one that matches the domain object to be validated.
			for (Validator validator : validatorList) {
				
				if (validator.supports(domain.getClass())) {
					// Call the validate method on the validator.
					ValidationUtils.invokeValidator(validator, domain, errors, hints);
				}
			}

			if (errors.hasErrors()) {
				
				// Using relection we will find all the @XmlElement annotations. Field error name,
				// those that failed validation should match the instance variable names in the 
				// class that failed validation.
				Field[] fields = domain.getClass().getDeclaredFields();
				
				List<ValidationError> validationErrors = new ArrayList<>();
				
				OUTER:
				// There are field errors and general object errors.
				for (ObjectError error : errors.getAllErrors()) {
					// Field error names are the instance variable names in the class that is being validated.
					if (error instanceof FieldError) {
						
						// Retrieve the field name that failed vaidation.
						String errorFieldName = ((FieldError) error).getField();
						
						// Iterate over all the reflective fields.
						for (Field field : fields) {
							
							// Compare the field names. 
							if (field.getName().equals(errorFieldName)) {
								XmlElement xmlElement = field.getAnnotation(XmlElement.class);
								
								if (xmlElement != null) {
									String xmlElementName = xmlElement.name();
									// Set the xml element name as the field name.
									validationErrors.add(new ValidationError(xmlElementName, error.getDefaultMessage()));
									continue OUTER;
								}
							}
						}
						// If an xml element annotation was not found for the name of the field that failed validation 
						// then use the error field name as the field name. NOTE: If this occurs then the setting of the 
						// error field name in the validation code does not match the field name on the actual object. The
						// validation code should change to match the field name on the object.
						validationErrors.add(new ValidationError(((FieldError) error).getField(), error.getDefaultMessage()));
					} else {
						validationErrors.add(new ValidationError(error.getDefaultMessage()));
					}
				}
				
				throw new ValidationException("Validation Failed for " + domain.getClass().getName(), validationErrors);
			}
		}
	}
}
