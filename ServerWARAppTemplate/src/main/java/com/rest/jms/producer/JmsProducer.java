package com.rest.jms.producer;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.rest.aspect.Event;

/**
 * A JMS producer that simply sets the event on a queue to be processed by a different
 * part of the application. Here we are striving for loose coupling using asynchronous 
 * communication between component services. 
 */
public class JmsProducer {

	private static final Logger LOG = LogManager.getLogger(JmsProducer.class);
	
	@Autowired
	private ConnectionFactory connectionFactory;
	
	@Autowired
	private Destination eventsQueue;

	public void send(Event event) {
		
		Connection conn = null;
		
		try {
			conn = connectionFactory.createConnection();
			Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer producer = session.createProducer(eventsQueue);
			ObjectMessage message = session.createObjectMessage(event);
			
			producer.send(message);
			
		} catch (Exception ex) {
			LOG.error("Exception sending jms message: " + event.toString(), ex);
		} finally {
			
			if (conn != null) {
				
				try {
					conn.close();
				} catch (JMSException e) {
					LOG.warn("Exception closing jms connection.", e);
				}
			}
		}
	}
}
