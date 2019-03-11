package com.rest.jms.consumer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rest.aspect.Event;

/**
 * A JMS consumer that simply logs the message. Here we can take the event and 
 * save it to a time series database like InfluxDB. Using a tool like Graphana
 * it can read and graph the event data from InfluxDB and see the activity of 
 * the request events in our application.
 */
public class JmsConsumer implements MessageListener {

	private static final Logger LOG = LogManager.getLogger(JmsConsumer.class);
	
	@Override
	public void onMessage(Message message) {
		Event event = null;
		try {
			ObjectMessage objectMessage = ((ObjectMessage) message);
			event = (Event) objectMessage.getObject();
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}
		
		LOG.info("Jms message received: {}", event.toString());
	}
}
