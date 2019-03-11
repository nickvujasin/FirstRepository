package com.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import com.rest.domain.Customer;
import com.rest.domain.Customers;

public class JacksonJsonTest {

	@Test
	public void test() throws IOException {
		Customer customer1 = new Customer("Nick", "Vujasin", "nick_vujasin@yahoo.com");
		customer1.setId(1);
		Customer customer2 = new Customer("Stella", "Vujasin", "stella_vujasin@yahoo.com");
		customer2.setId(2);
		
		List<Customer> customers = new ArrayList<>();
		customers.add(customer1);
		customers.add(customer2);
		
		// Jackson does not look at the Json or Jaxb annotations by default when creating Json. 
		// We must create an annotation inspector that first searches and applies Json annotations
		// and then searches and applies Jaxb annotations to build the final json.
		// This setup mimics what is done in the configuration of the application. The configuration
		// in the app is done automatically by simply including jersey-media-json-jackson in the 
		// classpath. This package is defined in the pom.xml.
		ObjectMapper objectMapper = new ObjectMapper();
		AnnotationIntrospector intr = new AnnotationIntrospectorPair(
				new JacksonAnnotationIntrospector(), 
				new JaxbAnnotationIntrospector(objectMapper.getTypeFactory()));
		// Usually we use same introspector(s) for both serialization and deserialization.
		objectMapper.setAnnotationIntrospector(intr);
				
		String toJsonCustomer = objectMapper.writeValueAsString(customer1);
		//System.out.println("To Json: " + toJsonCustomer);
		Customer fromJsonCustomer = objectMapper.readValue(toJsonCustomer, Customer.class); 
		//System.out.println("From Json: " + fromJsonCustomer);
		
		String toJsonCustomersCollection = objectMapper.writeValueAsString(customers);
		//System.out.println("\nTo Json Collection: " + toJsonCustomersCollection);
		List<Customer> fromJsonCustomersCollection = objectMapper.readValue(toJsonCustomersCollection, new TypeReference<List<Customer>>(){});
		//System.out.println("From Json Collection: " + fromJsonCustomersCollection);
		
		Customers wrapper = new Customers();
		wrapper.setCustomers(customers);
		
		String toJsonCustomersObject = objectMapper.writeValueAsString(wrapper);
		//System.out.println("\nTo Json Customers: " + toJsonCustomersObject);
		Customers fromJsonCustomersObject = objectMapper.readValue(toJsonCustomersObject, Customers.class); 
		//System.out.println("From Json Customers: " + fromJsonCustomersObject);
	}
}
