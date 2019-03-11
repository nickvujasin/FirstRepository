package com.rest.service.validation;

import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;

import com.rest.dao.CustomerDAO;
import com.rest.domain.Customer;

@Component
public class CustomerValidator implements SmartValidator {

	@Autowired
	private CustomerDAO customerDAO;

	@Override
	public boolean supports(Class<?> clazz) {
		return Customer.class.equals(clazz);
	}
	
	@Override
	public void validate(Object target, Errors errors) {
		this.validate(target, errors, new Object[] {});
	}
	
	@Override
	public void validate(Object target, Errors errors, Object... validationHints) {
		
		Customer customer = (Customer) target;

		if (!ObjectUtils.isEmpty(validationHints)) {
			
			// As for right now the only hints being passed in is a String.
			String operation = (String) validationHints[0];

			if (operation.equals("CREATE")) {
				List<Customer> existingCustomers = customerDAO.getCustomers();

				for (Customer existingCustomer : existingCustomers) {
					if (existingCustomer.getEmail().equalsIgnoreCase(customer.getEmail())) {
						errors.rejectValue("email", "", "The email is already taken");
					}
				}
			} else if (operation.equals("UPDATE")) {
				List<Customer> existingCustomers = customerDAO.getCustomers();

				for (Customer existingCustomer : existingCustomers) {
					if (existingCustomer.getEmail().equalsIgnoreCase(customer.getEmail()) 
							&& existingCustomer.getId() != customer.getId()) {
						errors.rejectValue("email", "", "The email is already taken");
					}
				}
			}
		}
		
		// This calls the validation for the annotated fields on the domain object.
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
	    Validator annotationValidator = factory.getValidator();
	    
	    for (ConstraintViolation<Customer> cv : annotationValidator.validate(customer)) {
	    	errors.rejectValue(cv.getPropertyPath().toString(), "", cv.getMessageTemplate());
	    }
	}
}
