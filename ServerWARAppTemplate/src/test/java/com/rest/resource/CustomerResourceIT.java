package com.rest.resource;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.junit.Test;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
import com.rest.domain.Customer;
import com.rest.domain.Customers;
import com.rest.exception.ErrorMessage;

/**
 * Integration Test.
 * 
 * <pre>
 * These tests run with 2 different HTTP clients: 
 * 1. HttpClient 
 * 2. Jersey's implementation of JAX-RS.
 * 
 * For EACH client this class tests:
 * 1. Creating a customer successfully, with a bad request, with bad credentials, with no credentials.
 * 2. Updating a customer successfully, with a bad request, with bad credentials, with no credentials.
 * 3. Deleting a customer successfully, with bad credentials, with no credentials.
 * 4. Getting a customer successfully, with a bad request, with bad credentials, with no credentials.
 * 5. Getting a list of customers successfully, with bad credentials, with no credentials.
 * </pre>
 */
public class CustomerResourceIT {

	@Test
	public void testGetCustomersHttpClientJson() throws ClientProtocolException, IOException {
		// Build the request.
		HttpGet request = new HttpGet(getBaseURI());

		// Basic Authorization.
		String auth = "admin" + ":" + "admin";
		byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.ISO_8859_1));
		String authHeader = "Basic " + new String(encodedAuth);
		request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
		request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.toString());

		try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
			HttpResponse response = client.execute(request);

			int statusCode = response.getStatusLine().getStatusCode();
			assertThat(statusCode, equalTo(HttpStatus.SC_OK));

			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				String result = entity != null ? EntityUtils.toString(entity) : null;
				EntityUtils.consume(entity);

				assertNotNull(result);

				ObjectMapper objectMapper = new ObjectMapper();
				AnnotationIntrospector intr = new AnnotationIntrospectorPair(
						new JacksonAnnotationIntrospector(), 
						new JaxbAnnotationIntrospector(objectMapper.getTypeFactory()));
				objectMapper.setAnnotationIntrospector(intr);

				Customers customers = objectMapper.readValue(result, Customers.class); 
				
				assertTrue(!customers.getCustomers().isEmpty());
			}
		}
	}

	@Test
	public void testGetCustomersHttpClientXml() throws IOException, JAXBException {
		// Build the request.
		HttpGet request = new HttpGet(getBaseURI());

		// Basic Authorization.
		String auth = "admin" + ":" + "admin";
		byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.ISO_8859_1));
		String authHeader = "Basic " + new String(encodedAuth);
		request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
		request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_XML.toString());

		try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
			HttpResponse response = client.execute(request);

			int statusCode = response.getStatusLine().getStatusCode();
			assertThat(statusCode, equalTo(HttpStatus.SC_OK));

			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				String result = entity != null ? EntityUtils.toString(entity) : null;
				EntityUtils.consume(entity);

				assertNotNull(result);

				JAXBContext jaxbContext = JAXBContext.newInstance(Customers.class);

				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				Customers customers = (Customers) jaxbUnmarshaller.unmarshal(new StringReader(result));

				assertTrue(!customers.getCustomers().isEmpty());
			}
		}
	}

	@Test
	public void testGetCustomersJerseyClientJson() {
		// Basic Authorization.
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("admin", "admin");

		ClientConfig config = new ClientConfig();
		config.register(feature);

		Client client = null;
		Response response = null;
		try {
			client = ClientBuilder.newClient(config);

			WebTarget target = client.target(getBaseURI());

			response = target.request().accept(MediaType.APPLICATION_JSON).get(Response.class);

			int statusCode = response.getStatus();
			assertThat(statusCode, equalTo(Response.Status.OK.getStatusCode()));

			if (statusCode == Response.Status.OK.getStatusCode()) {
				Customers customers = response.readEntity(Customers.class);

				assertTrue(!customers.getCustomers().isEmpty());
			}
		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.close();
			}
		}
	}

	@Test
	public void testGetCustomersJerseyClientXml() {
		// Basic Authorization.
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("admin", "admin");

		ClientConfig config = new ClientConfig();
		config.register(feature);

		Client client = null;
		Response response = null;

		try {
			client = ClientBuilder.newClient(config);

			WebTarget target = client.target(getBaseURI());

			response = target.request().accept(MediaType.APPLICATION_XML).get(Response.class);

			int statusCode = response.getStatus();
			assertThat(statusCode, equalTo(Response.Status.OK.getStatusCode()));

			if (statusCode == Response.Status.OK.getStatusCode()) {
				Customers customers = response.readEntity(Customers.class);

				assertTrue(!customers.getCustomers().isEmpty());
			}
		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.close();
			}
		}
	}

	@Test
	public void testGetCustomersHttpClientBadCredentials() throws ClientProtocolException, IOException {
		// Build the request.
		HttpGet request = new HttpGet(getBaseURI());

		// Basic Authorization.
		String auth = "bad" + ":" + "credentials";
		byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.ISO_8859_1));
		String authHeader = "Basic " + new String(encodedAuth);
		request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
		request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.toString());

		try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
			HttpResponse response = client.execute(request);

			int statusCode = response.getStatusLine().getStatusCode();
			assertThat(statusCode, equalTo(HttpStatus.SC_UNAUTHORIZED));
		}
	}

	@Test
	public void testGetCustomersHttpClientNoCredentials() throws ClientProtocolException, IOException {
		// Build the request.
		HttpGet request = new HttpGet(getBaseURI());
		request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.toString());

		try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
			HttpResponse response = client.execute(request);

			int statusCode = response.getStatusLine().getStatusCode();
			assertThat(statusCode, equalTo(HttpStatus.SC_UNAUTHORIZED));
		}
	}

	@Test
	public void testGetCustomersJerseyClientBadCredentials() {
		// Basic Authorization.
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("bad", "credentials");

		ClientConfig config = new ClientConfig();
		config.register(feature);

		Client client = null;
		Response response = null;
		try {
			client = ClientBuilder.newClient(config);

			WebTarget target = client.target(getBaseURI());

			response = target.request().accept(MediaType.APPLICATION_JSON).get(Response.class);

			int statusCode = response.getStatus();
			assertThat(statusCode, equalTo(Response.Status.UNAUTHORIZED.getStatusCode()));
		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.close();
			}
		}
	}

	@Test
	public void testGetCustomersJerseyClientNoCredentials() {
		ClientConfig config = new ClientConfig();

		Client client = null;
		Response response = null;
		try {
			client = ClientBuilder.newClient(config);

			WebTarget target = client.target(getBaseURI());

			response = target.request().accept(MediaType.APPLICATION_JSON).get(Response.class);

			int statusCode = response.getStatus();
			assertThat(statusCode, equalTo(Response.Status.UNAUTHORIZED.getStatusCode()));
		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.close();
			}
		}
	}

	@Test
	public void testGetCustomerHttpClientJson() throws ClientProtocolException, IOException, URISyntaxException {
		// Build the request.
		URIBuilder builder = new URIBuilder(getBaseURI().toString().concat("/2"));
		HttpGet request = new HttpGet(builder.build());

		// Basic Authorization.
		String auth = "admin" + ":" + "admin";
		byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.ISO_8859_1));
		String authHeader = "Basic " + new String(encodedAuth);
		request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
		request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.toString());

		try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
			HttpResponse response = client.execute(request);

			int statusCode = response.getStatusLine().getStatusCode();
			assertThat(statusCode, equalTo(HttpStatus.SC_OK));

			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				String result = entity != null ? EntityUtils.toString(entity) : null;
				EntityUtils.consume(entity);

				assertNotNull(result);

				ObjectMapper objectMapper = new ObjectMapper();
				AnnotationIntrospector intr = new AnnotationIntrospectorPair(
						new JacksonAnnotationIntrospector(), 
						new JaxbAnnotationIntrospector(objectMapper.getTypeFactory()));
				objectMapper.setAnnotationIntrospector(intr);				

				Customer customer = objectMapper.readValue(result, Customer.class); 

				assertEquals(customer.getId(), 2);
				assertEquals(customer.getFirstName(), "Stella");
				assertEquals(customer.getLastName(), "Vujasin");
				assertEquals(customer.getEmail(), "stella_vujasin@yahoo.com");
			}
		}
	}

	@Test
	public void testGetCustomerHttpClientXml() throws IOException, JAXBException, URISyntaxException {
		// Build the request.
		URIBuilder builder = new URIBuilder(getBaseURI().toString().concat("/2"));
		HttpGet request = new HttpGet(builder.build());

		// Basic Authorization.
		String auth = "admin" + ":" + "admin";
		byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.ISO_8859_1));
		String authHeader = "Basic " + new String(encodedAuth);
		request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
		request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_XML.toString());

		try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
			HttpResponse response = client.execute(request);

			int statusCode = response.getStatusLine().getStatusCode();
			assertThat(statusCode, equalTo(HttpStatus.SC_OK));

			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				String result = entity != null ? EntityUtils.toString(entity) : null;
				EntityUtils.consume(entity);

				assertNotNull(result);

				JAXBContext jaxbContext = JAXBContext.newInstance(Customer.class);

				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				Customer customer = (Customer) jaxbUnmarshaller.unmarshal(new StringReader(result));

				assertEquals(customer.getId(), 2);
				assertEquals(customer.getFirstName(), "Stella");
				assertEquals(customer.getLastName(), "Vujasin");
				assertEquals(customer.getEmail(), "stella_vujasin@yahoo.com");
			}
		}
	}

	@Test
	public void testGetCustomerJerseyClientJson() {
		// Basic Authorization.
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("admin", "admin");

		ClientConfig config = new ClientConfig();
		config.register(feature);

		Client client = null;
		Response response = null;
		try {
			client = ClientBuilder.newClient(config);

			WebTarget target = client.target(getBaseURI()).path("2");

			response = target.request().accept(MediaType.APPLICATION_JSON).get(Response.class);

			int statusCode = response.getStatus();
			assertThat(statusCode, equalTo(Response.Status.OK.getStatusCode()));

			if (statusCode == Response.Status.OK.getStatusCode()) {
				Customer customer = response.readEntity(Customer.class);

				assertEquals(customer.getId(), 2);
				assertEquals(customer.getFirstName(), "Stella");
				assertEquals(customer.getLastName(), "Vujasin");
				assertEquals(customer.getEmail(), "stella_vujasin@yahoo.com");
			}
		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.close();
			}
		}
	}

	@Test
	public void testGetCustomerJerseyClientXml() {
		// Basic Authorization.
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("admin", "admin");

		ClientConfig config = new ClientConfig();
		config.register(feature);

		Client client = null;
		Response response = null;

		try {
			client = ClientBuilder.newClient(config);

			WebTarget target = client.target(getBaseURI()).path("2");

			response = target.request().accept(MediaType.APPLICATION_XML).get(Response.class);

			int statusCode = response.getStatus();
			assertThat(statusCode, equalTo(Response.Status.OK.getStatusCode()));

			if (statusCode == Response.Status.OK.getStatusCode()) {
				Customer customer = response.readEntity(Customer.class);

				assertEquals(customer.getId(), 2);
				assertEquals(customer.getFirstName(), "Stella");
				assertEquals(customer.getLastName(), "Vujasin");
				assertEquals(customer.getEmail(), "stella_vujasin@yahoo.com");
			}
		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.close();
			}
		}
	}
	
	@Test
	public void testGetCustomerHttpClientBadCredentials() throws ClientProtocolException, IOException, URISyntaxException {
		// Build the request.
		URIBuilder builder = new URIBuilder(getBaseURI().toString().concat("/2"));
		HttpGet request = new HttpGet(builder.build());

		// Basic Authorization.
		String auth = "bad" + ":" + "credentials";
		byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.ISO_8859_1));
		String authHeader = "Basic " + new String(encodedAuth);
		request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
		request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.toString());

		try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
			HttpResponse response = client.execute(request);

			int statusCode = response.getStatusLine().getStatusCode();
			assertThat(statusCode, equalTo(HttpStatus.SC_UNAUTHORIZED));
		}
	}
	
	@Test
	public void testGetCustomerHttpClientNoCredentials() throws ClientProtocolException, IOException, URISyntaxException {
		// Build the request.
		URIBuilder builder = new URIBuilder(getBaseURI().toString().concat("/2"));
		HttpGet request = new HttpGet(builder.build());
		request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.toString());

		try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
			HttpResponse response = client.execute(request);

			int statusCode = response.getStatusLine().getStatusCode();
			assertThat(statusCode, equalTo(HttpStatus.SC_UNAUTHORIZED));
		}
	}
	
	@Test
	public void testGetCustomerJerseyClientBadCredentials() {
		// Basic Authorization.
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("bad", "credentials");

		ClientConfig config = new ClientConfig();
		config.register(feature);

		Client client = null;
		Response response = null;
		try {
			client = ClientBuilder.newClient(config);

			WebTarget target = client.target(getBaseURI()).path("2");

			response = target.request().accept(MediaType.APPLICATION_JSON).get(Response.class);

			int statusCode = response.getStatus();
			assertThat(statusCode, equalTo(Response.Status.UNAUTHORIZED.getStatusCode()));
		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.close();
			}
		}
	}
	
	@Test
	public void testGetCustomerJerseyClientNoCredentials() {
		ClientConfig config = new ClientConfig();
		
		Client client = null;
		Response response = null;
		try {
			client = ClientBuilder.newClient(config);

			WebTarget target = client.target(getBaseURI()).path("2");

			response = target.request().accept(MediaType.APPLICATION_JSON).get(Response.class);

			int statusCode = response.getStatus();
			assertThat(statusCode, equalTo(Response.Status.UNAUTHORIZED.getStatusCode()));
		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.close();
			}
		}
	}

	@Test
	public void testGetCustomerHttpClientJsonNotFoundException()
			throws ClientProtocolException, IOException, URISyntaxException {
		// Build the request.
		URIBuilder builder = new URIBuilder(getBaseURI().toString().concat("/5000"));
		HttpGet request = new HttpGet(builder.build());

		// Basic Authorization.
		String auth = "admin" + ":" + "admin";
		byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.ISO_8859_1));
		String authHeader = "Basic " + new String(encodedAuth);
		request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
		request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.toString());

		try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
			HttpResponse response = client.execute(request);

			int statusCode = response.getStatusLine().getStatusCode();
			assertThat(statusCode, equalTo(HttpStatus.SC_NOT_FOUND));

			if (statusCode == HttpStatus.SC_NOT_FOUND) {
				HttpEntity entity = response.getEntity();
				String result = entity != null ? EntityUtils.toString(entity) : null;
				EntityUtils.consume(entity);

				assertNotNull(result);

				ObjectMapper objectMapper = new ObjectMapper();
				AnnotationIntrospector intr = new AnnotationIntrospectorPair(
						new JacksonAnnotationIntrospector(), 
						new JaxbAnnotationIntrospector(objectMapper.getTypeFactory()));
				objectMapper.setAnnotationIntrospector(intr);
				
				ErrorMessage errorMessage = objectMapper.readValue(result, ErrorMessage.class); 
				
				assertEquals(errorMessage.getCode(), HttpStatus.SC_NOT_FOUND);
			}
		}
	}

	@Test
	public void testGetCustomerHttpClientXmlNotFoundException() throws IOException, JAXBException, URISyntaxException {
		// Build the request.
		URIBuilder builder = new URIBuilder(getBaseURI().toString().concat("/5000"));
		HttpGet request = new HttpGet(builder.build());

		// Basic Authorization.
		String auth = "admin" + ":" + "admin";
		byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.ISO_8859_1));
		String authHeader = "Basic " + new String(encodedAuth);
		request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
		request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_XML.toString());

		try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
			HttpResponse response = client.execute(request);

			int statusCode = response.getStatusLine().getStatusCode();
			assertThat(statusCode, equalTo(HttpStatus.SC_NOT_FOUND));

			if (statusCode == HttpStatus.SC_NOT_FOUND) {
				HttpEntity entity = response.getEntity();
				String result = entity != null ? EntityUtils.toString(entity) : null;
				EntityUtils.consume(entity);

				assertNotNull(result);

				JAXBContext jaxbContext = JAXBContext.newInstance(ErrorMessage.class);

				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				ErrorMessage errorMessage = (ErrorMessage) jaxbUnmarshaller.unmarshal(new StringReader(result));

				assertEquals(errorMessage.getCode(), HttpStatus.SC_NOT_FOUND);
			}
		}
	}

	@Test
	public void testGetCustomerJerseyClientJsonNotFoundException() {
		// Basic Authorization.
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("admin", "admin");

		ClientConfig config = new ClientConfig();
		config.register(feature);

		Client client = null;
		Response response = null;
		try {
			client = ClientBuilder.newClient(config);

			WebTarget target = client.target(getBaseURI()).path("5000");

			response = target.request().accept(MediaType.APPLICATION_JSON).get(Response.class);

			int statusCode = response.getStatus();
			assertThat(statusCode, equalTo(Response.Status.NOT_FOUND.getStatusCode()));

			if (statusCode == Response.Status.NOT_FOUND.getStatusCode()) {
				ErrorMessage errorMessage = response.readEntity(ErrorMessage.class);

				assertEquals(errorMessage.getCode(), Response.Status.NOT_FOUND.getStatusCode());
			}
		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.close();
			}
		}
	}

	@Test
	public void testGetCustomerJerseyClientXmlNotFoundException() {
		// Basic Authorization.
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("admin", "admin");

		ClientConfig config = new ClientConfig();
		config.register(feature);

		Client client = null;
		Response response = null;

		try {
			client = ClientBuilder.newClient(config);

			WebTarget target = client.target(getBaseURI()).path("5000");

			response = target.request().accept(MediaType.APPLICATION_XML).get(Response.class);

			int statusCode = response.getStatus();
			assertThat(statusCode, equalTo(Response.Status.NOT_FOUND.getStatusCode()));

			if (statusCode == Response.Status.NOT_FOUND.getStatusCode()) {
				ErrorMessage errorMessage = response.readEntity(ErrorMessage.class);

				assertEquals(errorMessage.getCode(), Response.Status.NOT_FOUND.getStatusCode());
			}
		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.close();
			}
		}
	}

	@Test
	public void testCreateCustomerHttpClientJson() throws ClientProtocolException, IOException, URISyntaxException {
		Customer createdCustomer = createCustomerHttpClientJson(
				new Customer("Tony", "DiMaso", "tony_dimaso@yahoo.com"));

		assertTrue(createdCustomer.getId() > 0);
		assertEquals(createdCustomer.getFirstName(), "Tony");
		assertEquals(createdCustomer.getLastName(), "DiMaso");
		assertEquals(createdCustomer.getEmail(), "tony_dimaso@yahoo.com");
	}

	@Test
	public void testCreateCustomerHttpClientXml() throws IOException, JAXBException, URISyntaxException {
		Customer createdCustomer = createCustomerHttpClientXml(new Customer("Pete", "Tutera", "pete_tutera@yahoo.com"));

		assertTrue(createdCustomer.getId() > 0);
		assertEquals(createdCustomer.getFirstName(), "Pete");
		assertEquals(createdCustomer.getLastName(), "Tutera");
		assertEquals(createdCustomer.getEmail(), "pete_tutera@yahoo.com");
	}

	@Test
	public void testCreateCustomerJerseyClientJson() {
		Customer createdCustomer = createCustomerJerseyClientJson(
				new Customer("Chris", "Tutera", "chris_tutera@yahoo.com"));

		assertTrue(createdCustomer.getId() > 0);
		assertEquals(createdCustomer.getFirstName(), "Chris");
		assertEquals(createdCustomer.getLastName(), "Tutera");
		assertEquals(createdCustomer.getEmail(), "chris_tutera@yahoo.com");
	}

	@Test
	public void testCreateCustomerJerseyClientXml() {
		Customer createdCustomer = createCustomerJerseyClientJson(new Customer("Mark", "Paul", "mark_paul@yahoo.com"));

		assertTrue(createdCustomer.getId() > 0);
		assertEquals(createdCustomer.getFirstName(), "Mark");
		assertEquals(createdCustomer.getLastName(), "Paul");
		assertEquals(createdCustomer.getEmail(), "mark_paul@yahoo.com");
	}
	
	@Test
	public void testCreateCustomerHttpClientBadCredentials()
			throws ClientProtocolException, IOException, URISyntaxException {
		// Build the request.
		URIBuilder builder = new URIBuilder(getBaseURI());
		HttpPost request = new HttpPost(builder.build());

		// Basic Authorization.
		String auth = "bad" + ":" + "credentials";
		byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.ISO_8859_1));
		String authHeader = "Basic " + new String(encodedAuth);
		request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
		request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.toString());
		request.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());

		try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
			// Here the first name is blank.
			Customer newCustomer = new Customer("Bad", "Creds", "bad_creds@yahoo.com");

			ObjectMapper objectMapper = new ObjectMapper();
			AnnotationIntrospector intr = new AnnotationIntrospectorPair(
					new JacksonAnnotationIntrospector(), 
					new JaxbAnnotationIntrospector(objectMapper.getTypeFactory()));
			objectMapper.setAnnotationIntrospector(intr);
			
			String jsonCustomer = objectMapper.writeValueAsString(newCustomer);
			
			// Set the request post body.
			StringEntity customerEntity = new StringEntity(jsonCustomer);
			request.setEntity(customerEntity);

			HttpResponse response = client.execute(request);

			int statusCode = response.getStatusLine().getStatusCode();
			assertThat(statusCode, equalTo(HttpStatus.SC_UNAUTHORIZED));
		}
	}
	
	@Test
	public void testCreateCustomerHttpClientNoCredentials()
			throws ClientProtocolException, IOException, URISyntaxException {
		// Build the request.
		URIBuilder builder = new URIBuilder(getBaseURI());
		HttpPost request = new HttpPost(builder.build());

		request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.toString());
		request.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());

		try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
			// Here the first name is blank.
			Customer newCustomer = new Customer("Bad", "Creds", "bad_creds@yahoo.com");

			ObjectMapper objectMapper = new ObjectMapper();
			AnnotationIntrospector intr = new AnnotationIntrospectorPair(
					new JacksonAnnotationIntrospector(), 
					new JaxbAnnotationIntrospector(objectMapper.getTypeFactory()));
			objectMapper.setAnnotationIntrospector(intr);
			
			String jsonCustomer = objectMapper.writeValueAsString(newCustomer);
			
			// Set the request post body.
			StringEntity customerEntity = new StringEntity(jsonCustomer);
			request.setEntity(customerEntity);

			HttpResponse response = client.execute(request);

			int statusCode = response.getStatusLine().getStatusCode();
			assertThat(statusCode, equalTo(HttpStatus.SC_UNAUTHORIZED));
		}
	}
	
	@Test
	public void testCreateCustomerJerseyClientBadCredentials() {
		// Basic Authorization.
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("bad", "credentials");

		ClientConfig config = new ClientConfig();
		config.register(feature);

		Client client = null;
		Response response = null;
		try {
			client = ClientBuilder.newClient(config);

			WebTarget target = client.target(getBaseURI());

			// Here the email is already taken.
			Customer newCustomer = new Customer("Bad", "Creds", "bad_creds@yahoo.com");

			response = target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
					.post(Entity.json(newCustomer), Response.class);

			int statusCode = response.getStatus();
			assertThat(statusCode, equalTo(Response.Status.UNAUTHORIZED.getStatusCode()));

		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.close();
			}
		}
	}
	
	@Test
	public void testCreateCustomerJerseyClientNoCredentials() {
		ClientConfig config = new ClientConfig();
		
		Client client = null;
		Response response = null;
		try {
			client = ClientBuilder.newClient(config);

			WebTarget target = client.target(getBaseURI());

			// Here the email is already taken.
			Customer newCustomer = new Customer("Bad", "Creds", "bad_creds@yahoo.com");

			response = target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
					.post(Entity.json(newCustomer), Response.class);

			int statusCode = response.getStatus();
			assertThat(statusCode, equalTo(Response.Status.UNAUTHORIZED.getStatusCode()));

		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.close();
			}
		}
	}

	@Test
	public void testCreateCustomerHttpClientJsonBadRequestException()
			throws ClientProtocolException, IOException, URISyntaxException {
		// Build the request.
		URIBuilder builder = new URIBuilder(getBaseURI());
		HttpPost request = new HttpPost(builder.build());

		// Basic Authorization.
		String auth = "admin" + ":" + "admin";
		byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.ISO_8859_1));
		String authHeader = "Basic " + new String(encodedAuth);
		request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
		request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.toString());
		request.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());

		try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
			// Here the first name is blank.
			Customer newCustomer = new Customer("", "DiMaso", "tony_dimaso@yahoo.com");

			ObjectMapper objectMapper = new ObjectMapper();
			AnnotationIntrospector intr = new AnnotationIntrospectorPair(
					new JacksonAnnotationIntrospector(), 
					new JaxbAnnotationIntrospector(objectMapper.getTypeFactory()));
			objectMapper.setAnnotationIntrospector(intr);
			
			String jsonCustomer = objectMapper.writeValueAsString(newCustomer);
			
			// Set the request post body.
			StringEntity customerEntity = new StringEntity(jsonCustomer);
			request.setEntity(customerEntity);

			HttpResponse response = client.execute(request);

			int statusCode = response.getStatusLine().getStatusCode();
			assertThat(statusCode, equalTo(HttpStatus.SC_BAD_REQUEST));

			if (statusCode == HttpStatus.SC_BAD_REQUEST) {
				HttpEntity entity = response.getEntity();
				String result = entity != null ? EntityUtils.toString(entity) : null;
				EntityUtils.consume(entity);

				assertNotNull(result);

				ErrorMessage errorMessage = objectMapper.readValue(result, ErrorMessage.class); 
				
				assertEquals(errorMessage.getCode(), HttpStatus.SC_BAD_REQUEST);
			}
		}
	}

	@Test
	public void testCreateCustomerHttpClientXmlBadRequestException()
			throws IOException, JAXBException, URISyntaxException {
		// Build the request.
		URIBuilder builder = new URIBuilder(getBaseURI());
		HttpPost request = new HttpPost(builder.build());

		// Basic Authorization.
		String auth = "admin" + ":" + "admin";
		byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.ISO_8859_1));
		String authHeader = "Basic " + new String(encodedAuth);
		request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
		request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_XML.toString());
		request.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_XML.toString());

		try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
			// Here the last name is blank.
			Customer newCustomer = new Customer("Pete", "", "pete_tutera@yahoo.com");

			// Convert the customer to xml.
			JAXBContext jaxbContext = JAXBContext.newInstance(Customer.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			StringWriter writer = new StringWriter();
			jaxbMarshaller.marshal(newCustomer, writer);

			// Set the request post body.
			StringEntity userEntity = new StringEntity(writer.getBuffer().toString());
			request.setEntity(userEntity);

			HttpResponse response = client.execute(request);

			int statusCode = response.getStatusLine().getStatusCode();
			assertThat(statusCode, equalTo(HttpStatus.SC_BAD_REQUEST));

			if (statusCode == HttpStatus.SC_BAD_REQUEST) {
				HttpEntity entity = response.getEntity();
				String result = entity != null ? EntityUtils.toString(entity) : null;
				EntityUtils.consume(entity);

				assertNotNull(result);

				jaxbContext = JAXBContext.newInstance(ErrorMessage.class);

				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				ErrorMessage errorMessage = (ErrorMessage) jaxbUnmarshaller.unmarshal(new StringReader(result));

				assertEquals(errorMessage.getCode(), HttpStatus.SC_BAD_REQUEST);
			}
		}
	}

	@Test
	public void testCreateCustomerJerseyClientJsonBadRequestException() {
		// Basic Authorization.
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("admin", "admin");

		ClientConfig config = new ClientConfig();
		config.register(feature);

		Client client = null;
		Response response = null;
		try {
			client = ClientBuilder.newClient(config);

			WebTarget target = client.target(getBaseURI());

			// Here the email is already taken.
			Customer newCustomer = new Customer("John", "Reagan", "nick_vujasin@yahoo.com");

			response = target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
					.post(Entity.json(newCustomer), Response.class);

			int statusCode = response.getStatus();
			assertThat(statusCode, equalTo(Response.Status.BAD_REQUEST.getStatusCode()));

			if (statusCode == Response.Status.BAD_REQUEST.getStatusCode()) {
				ErrorMessage errorMessage = response.readEntity(ErrorMessage.class);

				assertEquals(errorMessage.getCode(), Response.Status.BAD_REQUEST.getStatusCode());
			}

		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.close();
			}
		}
	}

	@Test
	public void testCreateCustomerJerseyClientXmlBadRequestException() {
		// Basic Authorization.
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("admin", "admin");

		ClientConfig config = new ClientConfig();
		config.register(feature);

		Client client = null;
		Response response = null;

		try {
			client = ClientBuilder.newClient(config);

			WebTarget target = client.target(getBaseURI());

			// Here the email is already taken.
			Customer newCustomer = new Customer("John", "Reagan", "nick_vujasin@yahoo.com");

			response = target.request(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML)
					.post(Entity.xml(newCustomer), Response.class);

			int statusCode = response.getStatus();
			assertThat(statusCode, equalTo(Response.Status.BAD_REQUEST.getStatusCode()));

			if (statusCode == Response.Status.BAD_REQUEST.getStatusCode()) {
				ErrorMessage errorMessage = response.readEntity(ErrorMessage.class);

				assertEquals(errorMessage.getCode(), Response.Status.BAD_REQUEST.getStatusCode());
			}
		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.close();
			}
		}
	}

	@Test
	public void testUpdateCustomerHttpClientJson() throws ClientProtocolException, IOException, URISyntaxException {
		Customer customer = createCustomerHttpClientJson(new Customer("A", "AA", "a_aa@yahoo.com"));

		// Build the request.
		URIBuilder builder = new URIBuilder(getBaseURI().toString().concat("/" + customer.getId()));
		HttpPut request = new HttpPut(builder.build());

		// Basic Authorization.
		String auth = "admin" + ":" + "admin";
		byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.ISO_8859_1));
		String authHeader = "Basic " + new String(encodedAuth);
		request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
		request.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());

		try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
			// Update the customers first name.
			customer.setFirstName("Z");

			ObjectMapper objectMapper = new ObjectMapper();
			AnnotationIntrospector intr = new AnnotationIntrospectorPair(
					new JacksonAnnotationIntrospector(), 
					new JaxbAnnotationIntrospector(objectMapper.getTypeFactory()));
			objectMapper.setAnnotationIntrospector(intr);
			
			String jsonCustomer = objectMapper.writeValueAsString(customer);
			
			// Set the request post body.
			StringEntity customerEntity = new StringEntity(jsonCustomer);
			request.setEntity(customerEntity);

			HttpResponse response = client.execute(request);

			int statusCode = response.getStatusLine().getStatusCode();
			assertThat(statusCode, equalTo(HttpStatus.SC_NO_CONTENT));
		}
	}

	@Test
	public void testUpdateCustomerHttpClientXml() throws IOException, JAXBException, URISyntaxException {
		Customer customer = createCustomerHttpClientXml(new Customer("B", "BB", "b_bb@yahoo.com"));

		// Build the request.
		URIBuilder builder = new URIBuilder(getBaseURI().toString().concat("/" + customer.getId()));
		HttpPut request = new HttpPut(builder.build());

		// Basic Authorization.
		String auth = "admin" + ":" + "admin";
		byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.ISO_8859_1));
		String authHeader = "Basic " + new String(encodedAuth);
		request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
		request.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_XML.toString());

		try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
			// Update the customers first name.
			customer.setFirstName("Z");

			// Convert the customer to xml.
			JAXBContext jaxbContext = JAXBContext.newInstance(Customer.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			StringWriter writer = new StringWriter();
			jaxbMarshaller.marshal(customer, writer);

			// Set the request post body.
			StringEntity userEntity = new StringEntity(writer.getBuffer().toString());
			request.setEntity(userEntity);

			HttpResponse response = client.execute(request);

			int statusCode = response.getStatusLine().getStatusCode();
			assertThat(statusCode, equalTo(HttpStatus.SC_NO_CONTENT));
		}
	}

	@Test
	public void testUpdateCustomerJerseyClientJson() {
		Customer customer = createCustomerJerseyClientJson(new Customer("C", "CC", "c_cc@yahoo.com"));

		// Basic Authorization.
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("admin", "admin");

		ClientConfig config = new ClientConfig();
		config.register(feature);

		Client client = null;
		Response response = null;
		try {
			client = ClientBuilder.newClient(config);

			WebTarget target = client.target(getBaseURI()).path(Integer.toString(customer.getId()));

			// Update the customers last name.
			customer.setLastName("ZZ");

			response = target.request(MediaType.APPLICATION_JSON).put(Entity.json(customer), Response.class);

			int statusCode = response.getStatus();
			assertThat(statusCode, equalTo(Response.Status.NO_CONTENT.getStatusCode()));
		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.close();
			}
		}
	}

	@Test
	public void testUpdateCustomerJerseyClientXml() {
		Customer customer = createCustomerJerseyClientXml(new Customer("D", "DD", "d_dd@yahoo.com"));

		// Basic Authorization.
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("admin", "admin");

		ClientConfig config = new ClientConfig();
		config.register(feature);

		Client client = null;
		Response response = null;

		try {
			client = ClientBuilder.newClient(config);

			WebTarget target = client.target(getBaseURI()).path(Integer.toString(customer.getId()));

			// Update the customers email.
			customer.setEmail("d_dddd@yahoo.com");

			response = target.request(MediaType.APPLICATION_XML).put(Entity.xml(customer), Response.class);

			int statusCode = response.getStatus();
			assertThat(statusCode, equalTo(Response.Status.NO_CONTENT.getStatusCode()));
		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.close();
			}
		}
	}

	@Test
	public void testUpdateCustomerHttpClientBadCredentials() throws ClientProtocolException, IOException, URISyntaxException {
		Customer customer = createCustomerHttpClientJson(new Customer("L", "LL", "l_ll@yahoo.com"));

		// Build the request.
		URIBuilder builder = new URIBuilder(getBaseURI().toString().concat("/" + customer.getId()));
		HttpPut request = new HttpPut(builder.build());

		// Basic Authorization.
		String auth = "bad" + ":" + "credentials";
		byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.ISO_8859_1));
		String authHeader = "Basic " + new String(encodedAuth);
		request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
		request.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());

		try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
			// Update the customers first name.
			customer.setFirstName("Z");

			ObjectMapper objectMapper = new ObjectMapper();
			AnnotationIntrospector intr = new AnnotationIntrospectorPair(
					new JacksonAnnotationIntrospector(), 
					new JaxbAnnotationIntrospector(objectMapper.getTypeFactory()));
			objectMapper.setAnnotationIntrospector(intr);
			
			String jsonCustomer = objectMapper.writeValueAsString(customer);
			
			// Set the request post body.
			StringEntity customerEntity = new StringEntity(jsonCustomer);
			request.setEntity(customerEntity);

			HttpResponse response = client.execute(request);

			int statusCode = response.getStatusLine().getStatusCode();
			assertThat(statusCode, equalTo(HttpStatus.SC_UNAUTHORIZED));
		}
	}
	
	@Test
	public void testUpdateCustomerHttpClientNoCredentials() throws ClientProtocolException, IOException, URISyntaxException {
		Customer customer = createCustomerHttpClientJson(new Customer("M", "MM", "m_mm@yahoo.com"));

		// Build the request.
		URIBuilder builder = new URIBuilder(getBaseURI().toString().concat("/" + customer.getId()));
		HttpPut request = new HttpPut(builder.build());
		request.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());

		try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
			// Update the customers first name.
			customer.setFirstName("Z");

			ObjectMapper objectMapper = new ObjectMapper();
			AnnotationIntrospector intr = new AnnotationIntrospectorPair(
					new JacksonAnnotationIntrospector(), 
					new JaxbAnnotationIntrospector(objectMapper.getTypeFactory()));
			objectMapper.setAnnotationIntrospector(intr);
			
			String jsonCustomer = objectMapper.writeValueAsString(customer);
			
			// Set the request post body.
			StringEntity customerEntity = new StringEntity(jsonCustomer);
			request.setEntity(customerEntity);

			HttpResponse response = client.execute(request);

			int statusCode = response.getStatusLine().getStatusCode();
			assertThat(statusCode, equalTo(HttpStatus.SC_UNAUTHORIZED));
		}
	}
	
	@Test
	public void testUpdateCustomerJerseyClientBadCredentials() {
		Customer customer = createCustomerJerseyClientJson(new Customer("N", "NN", "n_nn@yahoo.com"));

		// Basic Authorization.
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("bad", "credentials");

		ClientConfig config = new ClientConfig();
		config.register(feature);

		Client client = null;
		Response response = null;
		try {
			client = ClientBuilder.newClient(config);

			WebTarget target = client.target(getBaseURI()).path(Integer.toString(customer.getId()));

			// Update the customers last name.
			customer.setLastName("ZZ");

			response = target.request(MediaType.APPLICATION_JSON).put(Entity.json(customer), Response.class);

			int statusCode = response.getStatus();
			assertThat(statusCode, equalTo(Response.Status.UNAUTHORIZED.getStatusCode()));
		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.close();
			}
		}
	}
	
	@Test
	public void testUpdateCustomerJerseyClientNoCredentials() {
		Customer customer = createCustomerJerseyClientJson(new Customer("O", "OO", "o_oo@yahoo.com"));

		ClientConfig config = new ClientConfig();

		Client client = null;
		Response response = null;
		try {
			client = ClientBuilder.newClient(config);

			WebTarget target = client.target(getBaseURI()).path(Integer.toString(customer.getId()));

			// Update the customers last name.
			customer.setLastName("ZZ");

			response = target.request(MediaType.APPLICATION_JSON).put(Entity.json(customer), Response.class);

			int statusCode = response.getStatus();
			assertThat(statusCode, equalTo(Response.Status.UNAUTHORIZED.getStatusCode()));
		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.close();
			}
		}
	}
	
	@Test
	public void testUpdateCustomerHttpClientJsonBadRequestException()
			throws ClientProtocolException, IOException, URISyntaxException {
		Customer customer = createCustomerHttpClientJson(new Customer("E", "EE", "e_ee@yahoo.com"));

		// Build the request.
		URIBuilder builder = new URIBuilder(getBaseURI().toString().concat("/" + customer.getId()));
		HttpPut request = new HttpPut(builder.build());

		// Basic Authorization.
		String auth = "admin" + ":" + "admin";
		byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.ISO_8859_1));
		String authHeader = "Basic " + new String(encodedAuth);
		request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
		request.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());

		try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
			// Here the first name is blank.
			customer.setFirstName("");

			ObjectMapper objectMapper = new ObjectMapper();
			AnnotationIntrospector intr = new AnnotationIntrospectorPair(
					new JacksonAnnotationIntrospector(), 
					new JaxbAnnotationIntrospector(objectMapper.getTypeFactory()));
			objectMapper.setAnnotationIntrospector(intr);
			
			String jsonCustomer = objectMapper.writeValueAsString(customer);
			
			// Set the request post body.
			StringEntity customerEntity = new StringEntity(jsonCustomer);
			request.setEntity(customerEntity);

			HttpResponse response = client.execute(request);

			int statusCode = response.getStatusLine().getStatusCode();
			assertThat(statusCode, equalTo(HttpStatus.SC_BAD_REQUEST));

			if (statusCode == HttpStatus.SC_BAD_REQUEST) {
				HttpEntity entity = response.getEntity();
				String result = entity != null ? EntityUtils.toString(entity) : null;
				EntityUtils.consume(entity);

				assertNotNull(result);

				ErrorMessage errorMessage = objectMapper.readValue(result, ErrorMessage.class); 
				
				assertEquals(errorMessage.getCode(), HttpStatus.SC_BAD_REQUEST);
			}
		}
	}

	@Test
	public void testUpdateCustomerHttpClientXmlBadRequestException()
			throws IOException, JAXBException, URISyntaxException {
		Customer customer = createCustomerHttpClientXml(new Customer("F", "FF", "f_ff@yahoo.com"));

		// Build the request.
		URIBuilder builder = new URIBuilder(getBaseURI().toString().concat("/" + customer.getId()));
		HttpPut request = new HttpPut(builder.build());

		// Basic Authorization.
		String auth = "admin" + ":" + "admin";
		byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.ISO_8859_1));
		String authHeader = "Basic " + new String(encodedAuth);
		request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
		request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_XML.toString());
		request.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_XML.toString());

		try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
			// Here the last name is blank.
			customer.setLastName("");

			// Convert the customer to xml.
			JAXBContext jaxbContext = JAXBContext.newInstance(Customer.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			StringWriter writer = new StringWriter();
			jaxbMarshaller.marshal(customer, writer);

			// Set the request post body.
			StringEntity userEntity = new StringEntity(writer.getBuffer().toString());
			request.setEntity(userEntity);

			HttpResponse response = client.execute(request);

			int statusCode = response.getStatusLine().getStatusCode();
			assertThat(statusCode, equalTo(HttpStatus.SC_BAD_REQUEST));

			if (statusCode == HttpStatus.SC_BAD_REQUEST) {
				HttpEntity entity = response.getEntity();
				String result = entity != null ? EntityUtils.toString(entity) : null;
				EntityUtils.consume(entity);

				assertNotNull(result);

				jaxbContext = JAXBContext.newInstance(ErrorMessage.class);

				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				ErrorMessage errorMessage = (ErrorMessage) jaxbUnmarshaller.unmarshal(new StringReader(result));

				assertEquals(errorMessage.getCode(), HttpStatus.SC_BAD_REQUEST);
			}
		}
	}

	@Test
	public void testUpdateCustomerJerseyClientJsonBadRequestException() {
		Customer customer = createCustomerJerseyClientJson(new Customer("G", "GG", "g_gg@yahoo.com"));

		// Basic Authorization.
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("admin", "admin");

		ClientConfig config = new ClientConfig();
		config.register(feature);

		Client client = null;
		Response response = null;
		try {
			client = ClientBuilder.newClient(config);

			WebTarget target = client.target(getBaseURI()).path(Integer.toString(customer.getId()));

			// Here the email is already taken.
			customer.setEmail("nick_vujasin@yahoo.com");

			response = target.request(MediaType.APPLICATION_JSON).put(Entity.json(customer), Response.class);

			int statusCode = response.getStatus();
			assertThat(statusCode, equalTo(Response.Status.BAD_REQUEST.getStatusCode()));

			if (statusCode == Response.Status.BAD_REQUEST.getStatusCode()) {
				ErrorMessage errorMessage = response.readEntity(ErrorMessage.class);

				assertEquals(errorMessage.getCode(), Response.Status.BAD_REQUEST.getStatusCode());
			}

		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.close();
			}
		}
	}

	@Test
	public void testUpdateCustomerJerseyClientXmlBadRequestException() {
		Customer customer = createCustomerJerseyClientXml(new Customer("H", "HH", "h_hh@yahoo.com"));

		// Basic Authorization.
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("admin", "admin");

		ClientConfig config = new ClientConfig();
		config.register(feature);

		Client client = null;
		Response response = null;

		try {
			client = ClientBuilder.newClient(config);

			WebTarget target = client.target(getBaseURI()).path(Integer.toString(customer.getId()));

			// Here the email is already taken.
			customer.setEmail("nick_vujasin@yahoo.com");

			response = target.request(MediaType.APPLICATION_XML).put(Entity.xml(customer), Response.class);

			int statusCode = response.getStatus();
			assertThat(statusCode, equalTo(Response.Status.BAD_REQUEST.getStatusCode()));

			if (statusCode == Response.Status.BAD_REQUEST.getStatusCode()) {
				ErrorMessage errorMessage = response.readEntity(ErrorMessage.class);

				assertEquals(errorMessage.getCode(), Response.Status.BAD_REQUEST.getStatusCode());
			}
		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.close();
			}
		}
	}

	@Test
	public void testDeleteCustomerHttpClient() throws ClientProtocolException, IOException, URISyntaxException {
		Customer customer = createCustomerHttpClientJson(new Customer("I", "II", "i_ii@yahoo.com"));

		// Build the request.
		URIBuilder builder = new URIBuilder(getBaseURI().toString().concat("/" + customer.getId()));
		HttpDelete request = new HttpDelete(builder.build());

		// Basic Authorization.
		String auth = "admin" + ":" + "admin";
		byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.ISO_8859_1));
		String authHeader = "Basic " + new String(encodedAuth);
		request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);

		try (CloseableHttpClient client = HttpClientBuilder.create().build()) {

			HttpResponse response = client.execute(request);

			int statusCode = response.getStatusLine().getStatusCode();
			assertThat(statusCode, equalTo(HttpStatus.SC_NO_CONTENT));

			// Execute the request again. The result should be the same. The endpoint should
			// be idempotent.
			response = client.execute(request);

			statusCode = response.getStatusLine().getStatusCode();
			assertThat(statusCode, equalTo(HttpStatus.SC_NO_CONTENT));
		}
	}

	@Test
	public void testDeleteCustomerJerseyClient() {
		Customer customer = createCustomerJerseyClientJson(new Customer("J", "JJ", "j_jj@yahoo.com"));

		// Basic Authorization.
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("admin", "admin");

		ClientConfig config = new ClientConfig();
		config.register(feature);

		Client client = null;
		Response response = null;
		try {
			client = ClientBuilder.newClient(config);

			WebTarget target = client.target(getBaseURI()).path(Integer.toString(customer.getId()));

			response = target.request().delete();

			int statusCode = response.getStatus();
			assertThat(statusCode, equalTo(Response.Status.NO_CONTENT.getStatusCode()));
		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.close();
			}
		}

		try {
			client = ClientBuilder.newClient(config);

			WebTarget target = client.target(getBaseURI()).path(Integer.toString(customer.getId()));

			// Execute the request again. The result should be the same. The endpoint should
			// be idempotent.
			response = target.request().delete();

			int statusCode = response.getStatus();
			assertThat(statusCode, equalTo(Response.Status.NO_CONTENT.getStatusCode()));
		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.close();
			}
		}
	}
	
	@Test
	public void testDeleteCustomerHttpClientBadCredentials() throws ClientProtocolException, IOException, URISyntaxException {
		Customer customer = createCustomerHttpClientJson(new Customer("P", "PP", "p_pp@yahoo.com"));

		// Build the request.
		URIBuilder builder = new URIBuilder(getBaseURI().toString().concat("/" + customer.getId()));
		HttpDelete request = new HttpDelete(builder.build());

		// Basic Authorization.
		String auth = "bad" + ":" + "credentials";
		byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.ISO_8859_1));
		String authHeader = "Basic " + new String(encodedAuth);
		request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);

		try (CloseableHttpClient client = HttpClientBuilder.create().build()) {

			HttpResponse response = client.execute(request);

			int statusCode = response.getStatusLine().getStatusCode();
			assertThat(statusCode, equalTo(HttpStatus.SC_UNAUTHORIZED));
		}
	}
	
	@Test
	public void testDeleteCustomerHttpClientNoCredentials() throws ClientProtocolException, IOException, URISyntaxException {
		Customer customer = createCustomerHttpClientJson(new Customer("Q", "QQ", "q_qq@yahoo.com"));

		// Build the request.
		URIBuilder builder = new URIBuilder(getBaseURI().toString().concat("/" + customer.getId()));
		HttpDelete request = new HttpDelete(builder.build());

		try (CloseableHttpClient client = HttpClientBuilder.create().build()) {

			HttpResponse response = client.execute(request);

			int statusCode = response.getStatusLine().getStatusCode();
			assertThat(statusCode, equalTo(HttpStatus.SC_UNAUTHORIZED));
		}
	}

	private Customer createCustomerHttpClientJson(Customer customer) throws URISyntaxException, IOException {
		// Build the request.
		URIBuilder builder = new URIBuilder(getBaseURI());
		HttpPost request = new HttpPost(builder.build());

		// Basic Authorization.
		String auth = "admin" + ":" + "admin";
		byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.ISO_8859_1));
		String authHeader = "Basic " + new String(encodedAuth);
		request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
		request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.toString());
		request.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());

		Customer createdCustomer = null;

		try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
			// Convert the customer to json.
			
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

			String jsonCustomer = objectMapper.writeValueAsString(customer);
			
			// Set the request post body.
			StringEntity customerEntity = new StringEntity(jsonCustomer);
			request.setEntity(customerEntity);

			HttpResponse response = client.execute(request);

			int statusCode = response.getStatusLine().getStatusCode();
			assertThat(statusCode, equalTo(HttpStatus.SC_CREATED));

			if (statusCode == HttpStatus.SC_CREATED) {
				HttpEntity entity = response.getEntity();
				String result = entity != null ? EntityUtils.toString(entity) : null;
				EntityUtils.consume(entity);

				assertNotNull(result);

				createdCustomer = objectMapper.readValue(result, Customer.class);
			}
		}

		return createdCustomer;
	}

	private Customer createCustomerHttpClientXml(Customer customer)
			throws IOException, JAXBException, URISyntaxException {
		// Build the request.
		URIBuilder builder = new URIBuilder(getBaseURI());
		HttpPost request = new HttpPost(builder.build());

		// Basic Authorization.
		String auth = "admin" + ":" + "admin";
		byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.ISO_8859_1));
		String authHeader = "Basic " + new String(encodedAuth);
		request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
		request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_XML.toString());
		request.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_XML.toString());

		Customer createdCustomer = null;

		try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
			// Convert the customer to xml.
			JAXBContext jaxbContext = JAXBContext.newInstance(Customer.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			StringWriter writer = new StringWriter();
			jaxbMarshaller.marshal(customer, writer);

			// Set the request post body.
			StringEntity userEntity = new StringEntity(writer.getBuffer().toString());
			request.setEntity(userEntity);

			HttpResponse response = client.execute(request);

			int statusCode = response.getStatusLine().getStatusCode();
			assertThat(statusCode, equalTo(HttpStatus.SC_CREATED));

			if (statusCode == HttpStatus.SC_CREATED) {
				HttpEntity entity = response.getEntity();
				String result = entity != null ? EntityUtils.toString(entity) : null;
				EntityUtils.consume(entity);

				assertNotNull(result);

				jaxbContext = JAXBContext.newInstance(Customer.class);

				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				createdCustomer = (Customer) jaxbUnmarshaller.unmarshal(new StringReader(result));
			}
		}

		return createdCustomer;
	}

	private Customer createCustomerJerseyClientJson(Customer customer) {
		// Basic Authorization.
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("admin", "admin");

		ClientConfig config = new ClientConfig();
		config.register(feature);

		Client client = null;
		Response response = null;
		try {
			client = ClientBuilder.newClient(config);

			WebTarget target = client.target(getBaseURI());

			response = target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
					.post(Entity.json(customer), Response.class);

			int statusCode = response.getStatus();
			assertThat(statusCode, equalTo(Response.Status.CREATED.getStatusCode()));

			Customer createdCustomer = null;
			if (statusCode == Response.Status.CREATED.getStatusCode()) {
				createdCustomer = response.readEntity(Customer.class);
			}
			return createdCustomer;
		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.close();
			}
		}
	}

	private Customer createCustomerJerseyClientXml(Customer customer) {
		// Basic Authorization.
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("admin", "admin");

		ClientConfig config = new ClientConfig();
		config.register(feature);

		Client client = null;
		Response response = null;

		try {
			client = ClientBuilder.newClient(config);

			WebTarget target = client.target(getBaseURI());

			response = target.request(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML)
					.post(Entity.xml(customer), Response.class);

			int statusCode = response.getStatus();
			assertThat(statusCode, equalTo(Response.Status.CREATED.getStatusCode()));

			Customer createdCustomer = null;
			if (statusCode == Response.Status.CREATED.getStatusCode()) {
				createdCustomer = response.readEntity(Customer.class);
			}
			return createdCustomer;
		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.close();
			}
		}
	}
	
	private static URI getBaseURI() {
		return UriBuilder.fromUri("http://localhost:8080/rest/customers").build();
	}
}