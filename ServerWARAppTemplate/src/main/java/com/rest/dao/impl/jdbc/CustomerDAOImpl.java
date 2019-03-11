package com.rest.dao.impl.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.rest.dao.CustomerDAO;
import com.rest.dao.cache.CacheWrapper;
import com.rest.domain.Customer;

/*
 * Connections in our case are pooled (c3p0) so although it doesn't seem like it, we MUST 
 * close the pooled connection. It's actually a wrapper around the actual connection. It 
 * will under the covers release the actual connection back to the pool. It's up to the 
 * pool to decide whether the actual connection will actually be closed or be reused for 
 * a new getConnection() call. So, regardless of whether you're using a connection pool 
 * or not, you should always close all the JDBC resources in reversed order in the finally 
 * block of the try block where you've acquired them or beginning with Java 7 use try-with-resources
 * which does it automatically for you.
 * 
 * Prepared Statements are precompiled and stored to be used multiple times which makes 
 * them more performant.
 */
public class CustomerDAOImpl implements CustomerDAO {
	
	private static final Logger LOG = LogManager.getLogger(CustomerDAOImpl.class);
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private CacheWrapper<Integer, Customer> customerCache;
	
	@Override
	public Customer createCustomer(Customer customer) {
		
		String sql = "INSERT INTO Customers (first_name, last_name, email) VALUES (?, ?, ?)";
		
		try (Connection conn = dataSource.getConnection();
				PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			
			statement.setString(1, customer.getFirstName());
			statement.setString(2, customer.getLastName());
			statement.setString(3, customer.getEmail());
			 
			int count = statement.executeUpdate();
			if (count > 0) {
				long generatedKey = 0;
				
				ResultSet rs = statement.getGeneratedKeys();
				if (rs.next()) {
					generatedKey = rs.getLong(1);
				}
				
				// Set the generated key on the customer.
			    customer.setId((int) generatedKey);
			    
			    LOG.info("Successfully created Customer: {}", customer);
			}
			
		} catch (SQLException e) {
			LOG.error("Error creating Customer: {}", customer, e);
			throw new RuntimeException(e);
		}
		
		// Set the customer in the cache.
		customerCache.put(customer.getId(), customer);
		LOG.info("Set customer {} in the cache.", customer.getId());
		
		return customer;
	}

	@Override
	public void updateCustomer(int id, Customer customer) {
		
		String sql = "UPDATE Customers SET first_name = ?, last_name = ?, email = ? WHERE id = ?";
		
		try (Connection conn = dataSource.getConnection();
				PreparedStatement statement = conn.prepareStatement(sql)) {
			
			// Turning auto commit to false so that we manually have to call commit. 
			// This is done when there are many updates that need to occur atomically.
			conn.setAutoCommit(false);
			
			statement.setString(1, customer.getFirstName());
			statement.setString(2, customer.getLastName());
			statement.setString(3, customer.getEmail());
			statement.setInt(4, customer.getId());
			
			int count = statement.executeUpdate();
			 
			// Commit the update.
			conn.commit();
			
			if (count > 0) {
				LOG.info("Successfully updated Customer: {}", customer);
			}
			
		} catch (SQLException e) {
			LOG.error("Error updating Customer: {}", customer, e);
			throw new RuntimeException(e);
		}
		
		LOG.info("Updating customer {} in the cache.", customer.getId());
		// Update the customer in the cache.
		customerCache.put(customer.getId(), customer);
	}

	@Override
	public void deleteCustomer(int id) {
		
		String sql = "DELETE FROM Customers WHERE id = ?";

		try (Connection conn = dataSource.getConnection(); 
				PreparedStatement statement = conn.prepareStatement(sql)) {

			statement.setInt(1, id);

			int count = statement.executeUpdate();

			if (count > 0) {
				LOG.info("Successfully deleted Customer with id: {}", id);
			}

		} catch (SQLException e) {
			LOG.error("Error deleting Customer with id: {}", id, e);
			throw new RuntimeException(e);
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
		
		String sql = "SELECT * FROM Customers WHERE id = ?";
		
		try (Connection conn = dataSource.getConnection();
				PreparedStatement statement = conn.prepareStatement(sql)) {
			
			statement.setInt(1, id);
			
			ResultSet rs = statement.executeQuery();
			 
			if (rs.next()){
				customer = new Customer(rs.getString("first_name"), rs.getString("last_name"), rs.getString("email"));
	        	customer.setId(rs.getInt("id"));
			}
			
		} catch (SQLException e) {
			LOG.error("Error retreiving Customer with id: {}", id, e);
			throw new RuntimeException(e);
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
		
		String sql = "SELECT * FROM Customers";
		
		try (Connection conn = dataSource.getConnection();
				PreparedStatement statement = conn.prepareStatement(sql)) {
			
			ResultSet rs = statement.executeQuery();
			 
			while (rs.next()){
				Customer customer = new Customer(rs.getString("first_name"), rs.getString("last_name"), rs.getString("email"));
	        	customer.setId(rs.getInt("id"));
	        	customers.add(customer);
			}
			
		} catch (SQLException e) {
			LOG.error("Error retrieving all the Customers", e);
			throw new RuntimeException(e);
		}
		
		for (Customer customer : customers) {
			// Check if the customer is in the cache.
			if (!customerCache.containsKey(customer.getId())) {
				// Set the customer in the cache.
				customerCache.put(customer.getId(), customer);
				LOG.info("Set customer {} in the cache.", customer.getId());
			}
		}
		
		return customers;
	}
}
