package com.rest.dao.impl.hibernate;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.rest.dao.CustomerDAO;
import com.rest.domain.Customer;

public class CustomerDAOImpl implements CustomerDAO {
		
	private static final Logger LOG = LogManager.getLogger(CustomerDAOImpl.class);
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public Customer createCustomer(Customer customer) {
		Session session = sessionFactory.openSession();
		
		session.getTransaction().begin();
		
		session.save(customer);
		
		session.getTransaction().commit();
		session.close();
		
		LOG.info("Successfully created Customer: {}", customer);
		
		return customer;
	}

	@Override
	public void updateCustomer(int id, Customer customer) {
		Session session = sessionFactory.openSession();
		
		session.getTransaction().begin();
		
		Customer current = session.get(Customer.class, id);
		
		current.setFirstName(customer.getFirstName());
		current.setLastName(customer.getLastName());
		current.setEmail(customer.getEmail());
		
		session.update(current);
		
		session.getTransaction().commit();
		session.close();
		
		LOG.info("Successfully updated Customer: {}", customer);
	}

	@Override
	public void deleteCustomer(int id) {
		Session session = sessionFactory.openSession();
		
		session.getTransaction().begin();
		
		Customer customer = session.get(Customer.class, id);
		
		if (customer != null) {
			session.remove(customer);
		}
		
		session.getTransaction().commit();
		
		// Clearing out the object from L2C after a delete. If you don't do this, this call:
		// entityManagerFactory.getCache().contains(Customer.class, id); returns true.
		// Without the following statement, if you try to retrieve the customer object that just 
		// was deleted, it is not found which is the correct thing to do so we are putting this 
		// here just for the getCache().contains() to return the right response of false.
		sessionFactory.getCache().evict(Customer.class, id);
		
		LOG.info("Successfully deleted Customer with id: {}", id);
	}

	@Override
	public Customer getCustomer(int id) {
		Session session = sessionFactory.openSession();
		
		Customer customer = session.get(Customer.class, id);
		
		session.close();
		
		return customer;
	}

	@Override
	public List<Customer> getCustomers() {
		Session session = sessionFactory.openSession();
		
		// Make sure that the customers are cached by setting the hint.
		List<Customer> customers = (List<Customer>) session.createQuery("SELECT c FROM Customer c", Customer.class)
				.setHint("org.hibernate.cacheable", true).getResultList();
		
		session.close();
		
		return customers;
		
//		// Make sure that the customers are cached.
//		List<Customer> customers = null;
//		if (title == null) {
//			customers = (List<Customer>) session.createQuery("SELECT c FROM Customer c", Customer.class).setHint("org.hibernate.cacheable", true).getResultList();
//		} else {
//			customers = (List<Customer>) session.createQuery("SELECT c FROM Customer c WHERE c.email LIKE CONCAT('%',:email,'%')", Customer.class)
//					.setParameter("email", email).setHint("org.hibernate.cacheable", true).getResultList();
//		}
	}
}
