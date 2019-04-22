package com.rest.service.validation;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.validation.BeanPropertyBindingResult;

import com.rest.dao.CustomerDAO;
import com.rest.domain.Customer;

public class CustomerValidatorTest {

	@InjectMocks // Inject the Mock customerDAO into the CustomerValidator.
	private CustomerValidator customerValidator;

	@Mock // Mock the DAO instance
	private CustomerDAO customerDAO;

	@Rule // Create the mocks based on the @Mock annotation
	public MockitoRule rule = MockitoJUnit.rule();

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

	@Test
	public void validateCreateWithExistingEmail() {
		Customer customer = new Customer("Nick", "Vujasin", "nick_vujasin@yahoo.com");
		
		BeanPropertyBindingResult errors = new BeanPropertyBindingResult(customer, "Errors");
		
		Customer customer1 = new Customer("Stella", "Vujasin", "nick_vujasin@yahoo.com");
		customer1.setId(1);
		Customer customer2 = new Customer("Luka", "Vujasin", "luka_vujasin@yahoo.com");
		customer2.setId(2);
		
		List<Customer> customers = new ArrayList<>();
		customers.add(customer1);
		customers.add(customer2);
		
		when(customerDAO.getCustomers()).thenReturn(customers);
		
		// Call the validator.
		customerValidator.validate(customer, errors, ValidationOperation.CREATE);
		
		assertTrue(errors.getAllErrors().size() == 1);
		assertNotNull(errors.getFieldError("email"));
	}
	
	@Test
	public void validateUpdateWithExistingEmailError() {
		Customer customer = new Customer("Stella", "Vujasin", "nick_vujasin@yahoo.com");
		customer.setId(3);
		
		BeanPropertyBindingResult errors = new BeanPropertyBindingResult(customer, "Errors");
		
		Customer customer1 = new Customer("Nick", "Vujasin", "nick_vujasin@yahoo.com");
		customer1.setId(1);
		Customer customer2 = new Customer("Luka", "Vujasin", "luka_vujasin@yahoo.com");
		customer2.setId(2);
		Customer customer3 = new Customer("Stella", "Vujasin", "stella_vujasin@yahoo.com");
		customer3.setId(3);
		
		List<Customer> customers = new ArrayList<>();
		customers.add(customer1);
		customers.add(customer2);
		
		when(customerDAO.getCustomers()).thenReturn(customers);
		
		// Call the validator.
		customerValidator.validate(customer, errors, ValidationOperation.UPDATE);
		
		assertTrue(errors.getAllErrors().size() == 1);
		assertNotNull(errors.getFieldError("email"));
	}
	
	@Test
	public void validateWithMissingFirstName() {
		Customer customer = new Customer("", "Vujasin", "nick_vujasin@yahoo.com");
		
		BeanPropertyBindingResult errors = new BeanPropertyBindingResult(customer, "Errors");
		
		Customer customer1 = new Customer("Stella", "Vujasin", "stella_vujasin@yahoo.com");
		customer1.setId(1);
		Customer customer2 = new Customer("Luka", "Vujasin", "luka_vujasin@yahoo.com");
		customer2.setId(2);
		
		List<Customer> customers = new ArrayList<>();
		customers.add(customer1);
		customers.add(customer2);
		
		when(customerDAO.getCustomers()).thenReturn(customers);
		
		// Call the validator.
		customerValidator.validate(customer, errors, new Object[] {});
		
		assertTrue(errors.getAllErrors().size() == 1);
		assertNotNull(errors.getFieldError("firstName"));
	}
	
	@Test
	public void validateWithMissingLastName() {
		Customer customer = new Customer("Nick", "", "nick_vujasin@yahoo.com");
		
		BeanPropertyBindingResult errors = new BeanPropertyBindingResult(customer, "Errors");
		
		Customer customer1 = new Customer("Stella", "Vujasin", "stella_vujasin@yahoo.com");
		customer1.setId(1);
		Customer customer2 = new Customer("Luka", "Vujasin", "luka_vujasin@yahoo.com");
		customer2.setId(2);
		
		List<Customer> customers = new ArrayList<>();
		customers.add(customer1);
		customers.add(customer2);
		
		when(customerDAO.getCustomers()).thenReturn(customers);
		
		// Call the validator.
		customerValidator.validate(customer, errors, new Object[] {});
		
		assertTrue(errors.getAllErrors().size() == 1);
		assertNotNull(errors.getFieldError("lastName"));
	}
	
	@Test
	public void validateWithMissingEmail() {
		Customer customer = new Customer("Nick", "Vujasin", "");
		
		BeanPropertyBindingResult errors = new BeanPropertyBindingResult(customer, "Errors");
		
		Customer customer1 = new Customer("Stella", "Vujasin", "stella_vujasin@yahoo.com");
		customer1.setId(1);
		Customer customer2 = new Customer("Luka", "Vujasin", "luka_vujasin@yahoo.com");
		customer2.setId(2);
		
		List<Customer> customers = new ArrayList<>();
		customers.add(customer1);
		customers.add(customer2);
		
		when(customerDAO.getCustomers()).thenReturn(customers);
		
		// Call the validator.
		customerValidator.validate(customer, errors, new Object[] {});
		
		assertTrue(errors.getAllErrors().size() == 1);
		assertNotNull(errors.getFieldError("email"));
	}
	
	@Test
	public void validateWithMissingFirstNameLastNameEmail() {
		Customer customer = new Customer("", "", "");
		
		BeanPropertyBindingResult errors = new BeanPropertyBindingResult(customer, "Errors");
		
		Customer customer1 = new Customer("Stella", "Vujasin", "stella_vujasin@yahoo.com");
		customer1.setId(1);
		Customer customer2 = new Customer("Luka", "Vujasin", "luka_vujasin@yahoo.com");
		customer2.setId(2);
		
		List<Customer> customers = new ArrayList<>();
		customers.add(customer1);
		customers.add(customer2);
		
		when(customerDAO.getCustomers()).thenReturn(customers);
		
		// Call the validator.
		customerValidator.validate(customer, errors, new Object[] {});
		
		assertTrue(errors.getAllErrors().size() == 3);
	}
}
