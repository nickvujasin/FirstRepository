package com.rest.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.rest.dao.CustomerDAO;
import com.rest.domain.Customer;
import com.rest.service.CustomerService;
import com.rest.service.validation.DomainValidatorFactory;
import com.rest.service.validation.ValidationException;

public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private CustomerDAO customerDAO;

	@Autowired
	private DomainValidatorFactory validatorFactory;

	@Override
	public Customer createCustomer(Customer customer) throws ValidationException {
		validatorFactory.validateDomain(customer, "CREATE");
		return customerDAO.createCustomer(customer);
	}

	@Override
	public void updateCustomer(int id, Customer customer) throws ValidationException {
		validatorFactory.validateDomain(customer, "UPDATE");
		customerDAO.updateCustomer(id, customer);
	}

	@Override
	public void deleteCustomer(int id) {
		customerDAO.deleteCustomer(id);
	}

	@Override
	public Customer getCustomer(int id) {
		return customerDAO.getCustomer(id);
	}

	@Override
	public List<Customer> getCustomers() {
		return customerDAO.getCustomers();
	}
}
