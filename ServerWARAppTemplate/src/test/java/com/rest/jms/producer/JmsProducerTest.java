package com.rest.jms.producer;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.ws.rs.core.Response;

import org.apache.activemq.artemis.junit.EmbeddedJMSResource;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.rest.aspect.Event;
import com.rest.domain.Customer;

/**
 * This test class uses the JmsProvider to send messages to an embedded JMS instance. It also
 * plays the role of a listener (implements javax.jms.MessageListener) so it can compare the 
 * object (Event) that it sent against the object (Event) that it received in a single test.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/applicationContext-test.xml")
public class JmsProducerTest implements MessageListener {
 
	@Autowired
	private JmsProducer jmsProducer;
	
	@Rule
	// Adding the rule to a test will startup and automatically shut down an embedded JMS server.
	// The JMS server is running on IP: vm://0 which is configured in the applicationContext-text.xml.
	public EmbeddedJMSResource resource = new EmbeddedJMSResource();
	
	private String method = "GET";
	private String pathInfo = "/customers";
	
	@Test
	public void test() throws InterruptedException {
		
		// Build the mock HttpServletRequest.
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(method);
		request.addHeader("Accept", "application/json");
		request.setRemoteAddr("localhost");
		request.setPathInfo(pathInfo);
		
		// Start the event.
		Event sentEvent = new Event(request).start();
		
		// Create the customers.
		Customer customer1 = new Customer("Nick", "Vujasin", "nick_vujasin@yahoo.com");
		customer1.setId(1);
		Customer customer2 = new Customer("Luka", "Vujasin", "luka_vujasin@yahoo.com");
		customer2.setId(2);
		
		List<Customer> customers = new ArrayList<>();
		customers.add(customer1);
		customers.add(customer2);
		
		// Build the Response.
		Response response = Response.ok(customers).build(); // Returns a 200 OK with a list of customers.
		
		// Set the Response on the event.
		sentEvent.success(response);
		
		// Send the event using Jms.
		jmsProducer.send(sentEvent);
		
		Thread.sleep(3000);
	}
	
	@Override
	public void onMessage(Message message) {
		try {
			ObjectMessage objectMessage = ((ObjectMessage) message);
			Event receivedEvent = (Event) objectMessage.getObject();
		
			assertEquals(receivedEvent.getMethod(), method);
			assertEquals(receivedEvent.getResourceURI(), pathInfo);
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}
	}
}
