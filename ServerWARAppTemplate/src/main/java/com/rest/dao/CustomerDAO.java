package com.rest.dao;

import java.util.List;

import com.rest.domain.Customer;

public abstract interface CustomerDAO {

	public abstract Customer createCustomer(Customer customer);
	public abstract void updateCustomer(int id, Customer customer);
	public abstract void deleteCustomer(int id);
	public abstract Customer getCustomer(int id);
	public abstract List<Customer> getCustomers();
}
