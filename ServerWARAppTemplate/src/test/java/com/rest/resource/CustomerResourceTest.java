package com.rest.resource;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.mock.web.MockHttpServletRequest;

import com.rest.domain.Customer;
import com.rest.domain.Customers;
import com.rest.exception.BadRequestException;
import com.rest.exception.NotFoundException;
import com.rest.service.CustomerService;
import com.rest.service.validation.ValidationException;

public class CustomerResourceTest {

	@InjectMocks // Inject the Mock customerService into the CustomerResource.
	private CustomerResource customerResource;

	@Mock // Mock the service instance
	private CustomerService customerService;

	@Rule // Create the mocks based on the @Mock annotation
	public MockitoRule rule = MockitoJUnit.rule();

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

	@Test
	public void testGetCustomer() {
		Customer customer = new Customer("Nick", "Vujasin", "nick_vujasin@yahoo.com");
		customer.setId(1);

		// Set up customer service that when it receives a customer id of 1 it will
		// return the given customer.
		when(customerService.getCustomer(1)).thenReturn(customer);

		// Call the CustomerResource with a valid customer id.
		Response response = customerResource.getCustomer(1, new MockHttpServletRequest());

		assertEquals(customer.getEmail(), ((Customer) response.getEntity()).getEmail());
		assertEquals(200, response.getStatus());

		// Set up the customer service that when it receives a customer id of 100 it
		// will return null.
		when(customerService.getCustomer(100)).thenReturn(null);

		exceptionRule.expect(NotFoundException.class);
		exceptionRule.expectMessage("Not Found");

		// Call the CustomerResource with a customer id that does not exist.
		customerResource.getCustomer(100, new MockHttpServletRequest());
	}

	@Test
	public void testGetCustomers() {
		Customer customer1 = new Customer("Nick", "Vujasin", "nick_vujasin@yahoo.com");
		customer1.setId(1);
		Customer customer2 = new Customer("Luka", "Vujasin", "luka_vujasin@yahoo.com");
		customer2.setId(2);

		List<Customer> customers = new ArrayList<>();
		customers.add(customer1);
		customers.add(customer2);

		// Set up customer service to return the 2 customers defined above.
		when(customerService.getCustomers()).thenReturn(customers);

		// Create a request without the accept header populated.
		MockHttpServletRequest request = new MockHttpServletRequest();
		
		// Call the CustomerResource. The response defaults to json.
		Response response = customerResource.getCustomers(request);
		
		assertEquals(2, ((Customers) response.getEntity()).getCustomers().size());
		assertEquals(200, response.getStatus());
		
		// Create a request with the accept header as json.
		request = new MockHttpServletRequest();
		request.addHeader("Accept", "application/json");
		
		// Call the CustomerResource.
		response = customerResource.getCustomers(request);
		
		assertEquals(2, ((Customers) response.getEntity()).getCustomers().size());
		assertEquals(200, response.getStatus());
		
		// Create a request with the accept header as xml.
		request = new MockHttpServletRequest();
		request.addHeader("Accept", "application/xml");
		
		// Call the CustomerResource.
		response = customerResource.getCustomers(request);
		assertEquals(2, ((Customers) response.getEntity()).getCustomers().size());
		assertEquals(200, response.getStatus());
	}

	@Test
	public void testDeleteCustomer() {
		Response response = customerResource.deleteCustomer(1, new MockHttpServletRequest());
		assertEquals(204, response.getStatus());
	}

	@Test
	public void testCreateCustomer() throws ValidationException {
		Customer customer = new Customer("Nick", "Vujasin", "nick_vujasin@yahoo.com");

		// Set up customer service that when it receives a customer it will return the
		// added customer.
		when(customerService.createCustomer(customer)).thenReturn(customer);

		// Call the CustomerResource.
		Response response = customerResource.createCustomer(customer, new MockHttpServletRequest());

		assertEquals(customer.getEmail(), ((Customer) response.getEntity()).getEmail());
		assertEquals(201, response.getStatus());

		// Set up customer service to throw a validation exception.
		when(customerService.createCustomer(customer)).thenThrow(new ValidationException("Validation Error Message"));

		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Bad Request");

		// Call the CustomerResource.
		customerResource.createCustomer(customer, new MockHttpServletRequest());
	}

	@Test
	public void testUpdateCustomer() throws ValidationException {
		Customer customer = new Customer("Nick", "Vujasin", "nick_vujasin@yahoo.com");
		customer.setId(1);

		// Call the CustomerResource.
		Response response = customerResource.updateCustomer(1, customer, new MockHttpServletRequest());
		assertEquals(204, response.getStatus());

		// Set up customer service void method update customer to throw a validation
		// exception.
		doThrow(new ValidationException("Validation Error Message")).when(customerService).updateCustomer(1, customer);

		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("Bad Request");

		// Call the CustomerResource.
		customerResource.updateCustomer(1, customer, new MockHttpServletRequest());
	}
}
