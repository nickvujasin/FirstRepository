package com.rest.service;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.executable.ExecutableValidator;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.rest.service.impl.CustomerServiceImpl;

/** 
 * This class is really easy to test, it really is just a pass through between the REST and DAO layers that does validation.
 * 
 * Instead of doing validation tests here, the validation class can be tested without going through the Service layer. 
 */
public class CustomerServiceTest {
	
	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();
	
	// The ExecutableValidator interface offers these 2 methods for method validation:
    // validateParameters() and validateReturnValue()
	private static ExecutableValidator executableValidator;

    @BeforeClass
    public static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		executableValidator = factory.getValidator().forExecutables();
    }
    
    @Test
    public void listIsNullOnGetCustomers() throws NoSuchMethodException, SecurityException {

    	CustomerService customerServiceObject = new CustomerServiceImpl();
        Method getCustomersMethod = CustomerService.class.getMethod("getCustomers");
        Object returnValue = null;
 
        Set<ConstraintViolation<CustomerService>> violations = executableValidator.validateReturnValue(
        		customerServiceObject, getCustomersMethod, returnValue);

        assertEquals(1, violations.size());
        assertEquals(violations.iterator().next().getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(), "NotNull");
    }
}
