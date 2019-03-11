package com.rest.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

public class CustomersTest {

	@Test
	public void testXmlMarshallingAndUnmarshalling() throws JAXBException {
		Customer customer1 = new Customer("Nick", "Vujasin", "nick_vujasin@yahoo.com");
		customer1.setId(1);
		Customer customer2 = new Customer("Luka", "Vujasin", "luka_vujasin@yahoo.com");
		customer2.setId(2);

		List<Customer> customers = new ArrayList<>();
		customers.add(customer1);
		customers.add(customer2);

		Customers customersObjectIn = new Customers();
		customersObjectIn.setCustomers(customers);

		//System.out.println("Object to be Marshalled and Unmarshalled: " + customersObjectIn);

		JAXBContext jaxbContext = JAXBContext.newInstance(Customers.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		StringWriter writer = new StringWriter();
		jaxbMarshaller.marshal(customersObjectIn, writer);
		//System.out.println("Marshalled: " + writer.toString());

		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		Customers customersObjectOut = (Customers) jaxbUnmarshaller.unmarshal(new StringReader(writer.toString()));
		//System.out.println("Unmarshalled: " + customersObjectOut);
		
		assertEquals(customersObjectIn.getCustomers().size(), customersObjectOut.getCustomers().size());	
		
		int numberOfCustomers = 0;
		
		for (Customer customer : customersObjectOut.getCustomers()) {
			
			assertTrue(customer.getId() != 0);
			
			if (customer.getId() == 1) {
				assertEquals(customer.getFirstName(), customer1.getFirstName());
				assertEquals(customer.getLastName(), customer1.getLastName());
				assertEquals(customer.getEmail(), customer1.getEmail());
				++numberOfCustomers;
			}
			
			if (customer.getId() == 2) {
				assertEquals(customer.getFirstName(), customer2.getFirstName());
				assertEquals(customer.getLastName(), customer2.getLastName());
				assertEquals(customer.getEmail(), customer2.getEmail());
				++numberOfCustomers;
			}
		}
		
		assertTrue("Did not process the number of customers that the test should have.", numberOfCustomers == 2);
	}	
	
	@Test
	public void testJsonMarshallingAndUnmarshalling() throws IOException {
		Customer customer1 = new Customer("Nick", "Vujasin", "nick_vujasin@yahoo.com");
		customer1.setId(1);
		Customer customer2 = new Customer("Luka", "Vujasin", "luka_vujasin@yahoo.com");
		customer2.setId(2);

		List<Customer> customers = new ArrayList<>();
		customers.add(customer1);
		customers.add(customer2);

		Customers customersObjectIn = new Customers();
		customersObjectIn.setCustomers(customers);

		//System.out.println("Object to be Marshalled and Unmarshalled: " + customersObjectIn);

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
		
		String toJsonCustomersObject = objectMapper.writeValueAsString(customersObjectIn);
		// System.out.println("Marshalled: " + toJsonCustomersObject);
		
		assertTrue(toJsonCustomersObject.contains("customers"));
		
		Customers customersObjectOut = objectMapper.readValue(toJsonCustomersObject, Customers.class); 
		// System.out.println("Unmarshalled: " + fromJsonCustomersObject);
				
		assertEquals(customersObjectIn.getCustomers().size(), customersObjectOut.getCustomers().size());	
		
		int numberOfCustomers = 0;
		
		for (Customer customer : customersObjectOut.getCustomers()) {
			
			assertTrue(customer.getId() != 0);
			
			if (customer.getId() == 1) {
				assertEquals(customer.getFirstName(), customer1.getFirstName());
				assertEquals(customer.getLastName(), customer1.getLastName());
				assertEquals(customer.getEmail(), customer1.getEmail());
				++numberOfCustomers;
			}
			
			if (customer.getId() == 2) {
				assertEquals(customer.getFirstName(), customer2.getFirstName());
				assertEquals(customer.getLastName(), customer2.getLastName());
				assertEquals(customer.getEmail(), customer2.getEmail());
				++numberOfCustomers;
			}
		}
		
		assertTrue("Did not process the number of customers that the test should have.", numberOfCustomers == 2);
	}	
}
