package com.rest.domain;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

public class CustomerTest {

	private static Validator validator;

    @BeforeClass
    public static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void firstNameIsNull() {
        Customer customer = new Customer(null, "Vujasin", "nick_vujasin@yahoo.com");
        
        Set<ConstraintViolation<Customer>> constraintViolations =
                validator.validate(customer);

        assertEquals(1, constraintViolations.size());
        assertEquals("First Name must be populated", constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void firstNameIsEmpty() {
        Customer customer = new Customer("", "Vujasin", "nick_vujasin@yahoo.com");
        
        Set<ConstraintViolation<Customer>> constraintViolations =
                validator.validate(customer);

        assertEquals(1, constraintViolations.size());
        assertEquals("First Name must be populated", constraintViolations.iterator().next().getMessage());
    }
    
    @Test
    public void lastNameIsNull() {
        Customer customer = new Customer("Nick", null, "nick_vujasin@yahoo.com");
        
        Set<ConstraintViolation<Customer>> constraintViolations =
                validator.validate(customer);

        assertEquals(1, constraintViolations.size());
        assertEquals("Last Name must be populated", constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void lastNameIsEmpty() {
        Customer customer = new Customer("Nick", "", "nick_vujasin@yahoo.com");
        
        Set<ConstraintViolation<Customer>> constraintViolations =
                validator.validate(customer);

        assertEquals(1, constraintViolations.size());
        assertEquals("Last Name must be populated", constraintViolations.iterator().next().getMessage());
    }
    
    @Test
    public void emailIsNull() {
        Customer customer = new Customer("Nick", "Vujasin", null);
        
        Set<ConstraintViolation<Customer>> constraintViolations =
                validator.validate(customer);

        assertEquals(1, constraintViolations.size());
        assertEquals("Email must be populated", constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void emailIsEmpty() {
        Customer customer = new Customer("Nick", "Vujasin", "");
        
        Set<ConstraintViolation<Customer>> constraintViolations =
                validator.validate(customer);

        assertEquals(1, constraintViolations.size());
        assertEquals("Email must be populated", constraintViolations.iterator().next().getMessage());
    }
    
    @Test
    public void emailIsFormattedIncorrectly() {
        Customer customer = new Customer("Nick", "Vujasin", "nick_vujasin.yahoo.com");
        
        Set<ConstraintViolation<Customer>> constraintViolations =
                validator.validate(customer);

        assertEquals(1, constraintViolations.size());
        assertEquals("Email must be valid", constraintViolations.iterator().next().getMessage());
    }
    
    @Test
    public void customerIsValid() {
        Customer customer = new Customer("Nick", " Vujasin", "nick_vujasin@yahoo.com");

        Set<ConstraintViolation<Customer>> constraintViolations =
                validator.validate( customer );

        assertEquals( 0, constraintViolations.size() );
    }
    
    @Test
	public void testXmlMarshallingAndUnmarshalling() throws JAXBException {
		Customer customerIn = new Customer("Nick", "Vujasin", "nick_vujasin@yahoo.com");
		customerIn.setId(1);
		
		//System.out.println("Object to be Marshalled and Unmarshalled: " + customerIn);

		JAXBContext jaxbContext = JAXBContext.newInstance(Customer.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		StringWriter writer = new StringWriter();
		jaxbMarshaller.marshal(customerIn, writer);
		//System.out.println("Marshalled: " + writer.toString());

		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		Customer customerOut = (Customer) jaxbUnmarshaller.unmarshal(new StringReader(writer.toString()));
		//System.out.println("Unmarshalled: " + customerOut);
		
		assertEquals(customerIn.getId(), customerOut.getId());
		assertEquals(customerIn.getFirstName(), customerOut.getFirstName());
		assertEquals(customerIn.getLastName(), customerOut.getLastName());
		assertEquals(customerIn.getEmail(), customerOut.getEmail());
	}	
    
    @Test
	public void testJsonMarshallingAndUnmarshalling() throws IOException {
		Customer customerIn = new Customer("Nick", "Vujasin", "nick_vujasin@yahoo.com");
		customerIn.setId(1);
		
		//System.out.println("Object to be Marshalled and Unmarshalled: " + customerIn);

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
		
		String toJsonCustomer = objectMapper.writeValueAsString(customerIn);
		//System.out.println("Marshalled: " + toJsonCustomer);
		
		Customer customerOut = objectMapper.readValue(toJsonCustomer, Customer.class); 
		//System.out.println("Unmarshalled: " + customerOut);
				
		assertEquals(customerIn.getId(), customerOut.getId());
		assertEquals(customerIn.getFirstName(), customerOut.getFirstName());
		assertEquals(customerIn.getLastName(), customerOut.getLastName());
		assertEquals(customerIn.getEmail(), customerOut.getEmail());
	}	
}
