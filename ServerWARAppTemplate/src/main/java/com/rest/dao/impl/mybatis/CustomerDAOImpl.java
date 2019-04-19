package com.rest.dao.impl.mybatis;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.rest.dao.CustomerDAO;
import com.rest.dao.cache.CacheWrapper;
import com.rest.domain.Customer;

/**
 * Use MyBatis, if
 *
 * - you want to create your own SQL's and you are willing to maintain them.
 * - your environment is driven by relational data model.
 * - you have to work on existing and complex schemas.
 *
 * Use Hibernate, if the environment is driven by object model and needs to generate SQL automatically.
 */
public class CustomerDAOImpl implements CustomerDAO {
	
	private static final Logger LOG = LogManager.getLogger(CustomerDAOImpl.class);
	
	private static final String DAO_NAMESPACE = "Customer.";
	
	@Autowired
	private SqlSessionFactory sqlSessionFactory;
	
	@Autowired
	private CacheWrapper<Integer, Customer> customerCache;
	
	private SqlSession getSqlSession()
	{
		return sqlSessionFactory.openSession();
	}
	
	private String getNamespace()
	{
		return DAO_NAMESPACE;
	}
	
	@Override
	public Customer createCustomer(Customer customer) {
		
		try (SqlSession sqlSession = getSqlSession()) {
			int count = sqlSession.insert(getNamespace() + "add", customer);
			sqlSession.commit();
			
			if (count > 0) {
				LOG.info("Successfully created Customer: {}", customer);
			}
		}
		
		// Set the customer in the cache.
		customerCache.put(customer.getId(), customer);
		LOG.info("Set customer {} in the cache.", customer.getId());
		
		return customer;
	}

	@Override
	public void updateCustomer(int id, Customer customer) {
		
		try (SqlSession sqlSession = getSqlSession()) {
			int count = sqlSession.update(getNamespace() + "update", customer);
			sqlSession.commit();
			
			if (count > 0) {
				LOG.info("Successfully updated Customer: {}", customer);
			}
		}
		
		LOG.info("Updating customer {} in the cache.", customer.getId());
		// Update the customer in the cache.
		customerCache.put(customer.getId(), customer);
	}

	@Override
	public void deleteCustomer(int id) {
		
		try (SqlSession sqlSession = getSqlSession()) {
			Integer count = sqlSession.delete(getNamespace() + "remove", id);
			sqlSession.commit();

			if (count > 0) {
				LOG.info("Successfully deleted Customer with id: {}", id);
			}
		}
		
		LOG.info("Deleting customer {} from the cache.", id);
		// Delete the customer from the cache.
		customerCache.remove(id);
	}

	@Override
	public Customer getCustomer(int id) {
		
		// Check the cache.
		Customer customer = customerCache.get(id);
		if (customer != null) {
			LOG.info("Found customer {} in cache.",  id);
			return customer;
		}
		LOG.info("Did not find customer {} in cache.", id);
		
		try (SqlSession sqlSession = getSqlSession()) {
			customer = sqlSession.selectOne(getNamespace() + "get", id);
		}
		
		// Set the customer in the cache if found.
		if (customer != null) {
			// Set the customer in the cache.
			customerCache.put(customer.getId(), customer);
			LOG.info("Set customer {} in the cache.", customer.getId());
		}
		
		return customer;	
	}

	@Override
	public List<Customer> getCustomers() {
		
		List<Customer> customers = new ArrayList<>();
		
		try (SqlSession sqlSession = getSqlSession()) {
			customers = sqlSession.selectList(getNamespace() + "getAll");
		}
		
		for (Customer customer : customers) {
			// Check if the customer is in the cache.
			if (customerCache.putIfAbsent(customer.getId(), customer)) {
				// Set the customer in the cache.
				LOG.info("Set customer {} in the cache.", customer.getId());
			}
		}
		
		return customers;
	}
}
