package com.rest.dao.impl.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.rest.dao.CustomerDAO;
import com.rest.domain.Customer;

/**
 * This test class tests the customer DAO against an in memory H2 database and 
 * tests caching.
 */
public class CustomerDAOImplTest extends BaseDAOTest {
	
	@Autowired
	private CustomerDAO customerDAO;
	
	@Test
	public void testGetCustomers() {
		// There shouldn't be anything in the cache.
		assertFalse(sessionFactory.getCache().contains(Customer.class, 1));
		assertFalse(sessionFactory.getCache().contains(Customer.class, 2));
		
		// Retrieve the data from the DB.
		List<Customer> customers = customerDAO.getCustomers();
		assertEquals(customers.size(), 2);
		
		// Both entities should exist in the cache.
		assertTrue(sessionFactory.getCache().contains(Customer.class, 1));
		assertTrue(sessionFactory.getCache().contains(Customer.class, 2));
	}
	
	@Test
	public void testGetCustomer() {
		// There shouldn't be anything in the cache.
		assertFalse(sessionFactory.getCache().contains(Customer.class, 1));
		
		// Retrieve the data from the DB.
		Customer customer = customerDAO.getCustomer(1);
		assertEquals(customer.getEmail(), "nick_vujasin@yahoo.com");
		
		// The entity should exist in the cache.
		assertTrue(sessionFactory.getCache().contains(Customer.class, 1));
	}
	
	@Test
	public void testCreateCustomer() {
		Customer customer = new Customer("Luka", "Vujasin", "luka_vujasin@yahoo.com");
		Customer newCustomer = customerDAO.createCustomer(customer);
		assertTrue(newCustomer.getId() > 0);
		
		// Creating an object does not set it in the cache.
		assertFalse(sessionFactory.getCache().contains(Customer.class, newCustomer.getId()));
		
		List<Customer> customers = customerDAO.getCustomers();
		assertEquals(customers.size(), 3);
		
		// All entities should exist in the cache.
		customers.stream().forEach(c -> assertTrue(sessionFactory.getCache().contains(Customer.class, c.getId())));
	}
	
	@Test
	public void testUpdateCustomer() {
		// There shouldn't be anything in the cache.
		assertFalse(sessionFactory.getCache().contains(Customer.class, 1));
				
		Customer customer = customerDAO.getCustomer(1);
		customer.setEmail("nikola_vujasin@yahoo.com");
	
		// The entity should exist in the cache.
		assertTrue(sessionFactory.getCache().contains(Customer.class, 1));
			
		// The Customer object has the caching strategy of READ_WRITE through annotations.
		// READ_WRITE: This strategy guarantees strong consistency which it achieves by 
		// using ‘soft’ locks: When a cached entity is updated, a soft lock is stored 
		// in the cache for that entity as well, which is released after the transaction 
		// is committed. All concurrent transactions that access soft-locked entries will 
		// fetch the corresponding data directly from database.
		customerDAO.updateCustomer(1, customer);
		
		// The entity should exist in the cache.
		assertTrue(sessionFactory.getCache().contains(Customer.class, 1));
				
		// Looking at the logs, this retrieval of the updated customer is from the cache.
		// This means that the customer was updated in the cache after the update to the 
		// database which aligns with what the caching strategy of READ_WRITE states.
		Customer updatedCustomer = customerDAO.getCustomer(1);
		assertEquals(customer.getEmail(), updatedCustomer.getEmail());
		
		// The entity should exist in the cache.
		assertTrue(sessionFactory.getCache().contains(Customer.class, 1));		
	}
	
	@Test
	public void testDeleteCustomer() throws InterruptedException {
		// There shouldn't be anything in the cache.
		assertFalse(sessionFactory.getCache().contains(Customer.class, 1));
		
		Customer customer = customerDAO.getCustomer(1);
		assertNotNull(customer);
		
		// The entity should exist in the cache.
		assertTrue(sessionFactory.getCache().contains(Customer.class, 1));
		
		customerDAO.deleteCustomer(1);
		
		// There shouldn't be anything in the cache.
		assertFalse(sessionFactory.getCache().contains(Customer.class, 1));
				
		Customer deletedCustomer = customerDAO.getCustomer(1);
		assertNull(deletedCustomer);
		
		// There shouldn't be anything in the cache.
		assertFalse(sessionFactory.getCache().contains(Customer.class, 1));
	}
}
