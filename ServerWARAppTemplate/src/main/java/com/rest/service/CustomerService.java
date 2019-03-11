package com.rest.service;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.rest.domain.Customer;
import com.rest.service.validation.ValidationException;

/**
 * Interface that has the signatures of all the methods supported for the Customer domain object.
 */
public abstract interface CustomerService {

	public abstract Customer createCustomer(Customer customer) throws ValidationException;
	public abstract void updateCustomer(int id, Customer customer) throws ValidationException;
	public abstract void deleteCustomer(int id);
	public abstract Customer getCustomer(int id);
	@NotNull // Validation, the returned List<Customer> should not be null.
	public abstract List<Customer> getCustomers();
}
