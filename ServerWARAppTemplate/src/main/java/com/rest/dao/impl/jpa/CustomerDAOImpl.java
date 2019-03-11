package com.rest.dao.impl.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.rest.dao.CustomerDAO;
import com.rest.domain.Customer;

public class CustomerDAOImpl implements CustomerDAO {
		
	private static final Logger LOG = LogManager.getLogger(CustomerDAOImpl.class);
	
	@Autowired
	private EntityManagerFactory entityManagerFactory;
	
	@Override
	public Customer createCustomer(Customer customer) {
		EntityManager em = entityManagerFactory.createEntityManager();
		
		em.getTransaction().begin();
		
		em.persist(customer);
		
		em.getTransaction().commit();
		em.close();
		
		LOG.info("Successfully created Customer: {}", customer);
		
		return customer;
	}

	@Override
	public void updateCustomer(int id, Customer customer) {
		EntityManager em = entityManagerFactory.createEntityManager();
		
		em.getTransaction().begin();
		
		Customer current = em.find(Customer.class, id);
		
		current.setFirstName(customer.getFirstName());
		current.setLastName(customer.getLastName());
		current.setEmail(customer.getEmail());
		
		em.getTransaction().commit();
		em.close();
		
		LOG.info("Successfully updated Customer: {}", customer);
	}

	@Override
	public void deleteCustomer(int id) {
		EntityManager em = entityManagerFactory.createEntityManager();
		
		em.getTransaction().begin();
		
		Customer customer = em.find(Customer.class, id);
		
		if (customer != null) {
			em.remove(customer);
		}
		
		em.getTransaction().commit();
		
		// Clearing out the object from L2C after a delete. If you don't do this, this call:
		// entityManagerFactory.getCache().contains(Customer.class, id); returns true.
		// Without the following statement, if you try to retrieve the customer object that just 
		// was deleted, it is not found which is the correct thing to do so we are putting this 
		// here just for the getCache().contains() to return the right response of false.
		entityManagerFactory.getCache().evict(Customer.class, id);
		
		LOG.info("Successfully deleted Customer with id: {}", id);
	}

	@Override
	public Customer getCustomer(int id) {
		EntityManager em = entityManagerFactory.createEntityManager();
		
		Customer customer = em.find(Customer.class, id);
		
		em.close();
		
		return customer;
	}

	@Override
	public List<Customer> getCustomers() {
		EntityManager em = entityManagerFactory.createEntityManager();
		
		// Make sure that the customers are cached by setting the hint.
		List<Customer> customers = (List<Customer>) em.createQuery("SELECT c FROM Customer c", Customer.class)
				.setHint("org.hibernate.cacheable", true).getResultList();
		
		em.close();
		
		return customers;
		
//		// Make sure that the customers are cached.
//		List<Customer> customers = null;
//		if (title == null) {
//			customers = (List<Customer>) em.createQuery("SELECT c FROM Customer c", Customer.class).setHint("org.hibernate.cacheable", true).getResultList();
//		} else {
//			customers = (List<Customer>) em.createQuery("SELECT c FROM Customer c WHERE c.email LIKE CONCAT('%',:email,'%')", Customer.class)
//					.setParameter("email", email).setHint("org.hibernate.cacheable", true).getResultList();
//		}
	}
}
