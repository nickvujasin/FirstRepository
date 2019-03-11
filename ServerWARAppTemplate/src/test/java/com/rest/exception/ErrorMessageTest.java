package com.rest.exception;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;
import java.io.StringWriter;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

public class ErrorMessageTest {

	@Test
	public void testXmlMarshallingAndUnmarshalling() throws JAXBException {
		ErrorMessage errorMessageIn = new ErrorMessage(
				Response.Status.NOT_FOUND.getStatusCode(), 
				"Customer not found", "http://localhost:8080/error404.jsp", Response.Status.NOT_FOUND.getReasonPhrase());
		
		//System.out.println("Object to be Marshalled and Unmarshalled: " + errorMessageIn);

		JAXBContext jaxbContext = JAXBContext.newInstance(ErrorMessage.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		StringWriter writer = new StringWriter();
		jaxbMarshaller.marshal(errorMessageIn, writer);
		//System.out.println("Marshalled: " + writer.toString());

		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		ErrorMessage errorMessageOut = (ErrorMessage) jaxbUnmarshaller.unmarshal(new StringReader(writer.toString()));
		//System.out.println("Unmarshalled: " + errorMessageOut);
		
		assertEquals(errorMessageIn.getCode(), errorMessageOut.getCode());
		assertEquals(errorMessageIn.getDescription(), errorMessageOut.getDescription());
		assertEquals(errorMessageIn.getLink(), errorMessageOut.getLink());
		assertEquals(errorMessageIn.getStatus(), errorMessageOut.getStatus());
	}	
}
