package com.rest.resource;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.rest.domain.Customer;
import com.rest.domain.Customers;
import com.rest.exception.BadRequestException;
import com.rest.exception.ErrorMessage;
import com.rest.exception.InternalServerErrorException;
import com.rest.exception.NotFoundException;
import com.rest.service.CustomerService;
import com.rest.service.validation.ValidationException;
import com.webcohesion.enunciate.metadata.rs.TypeHint;

/*
 * With respect to scan-auto-detection and dependency injection for
 * BeanDefinition all these
 * annotations @Component, @Service, @Repository, @Controller are the same. We
 * can use one in place of another and can still get our way around.
 * 
 * In spring autowiring, @Autowired annotation handles only wiring part. We
 * still have to define the beans so the container is aware of them and can
 * inject them for you. With these annotations in place and automatic component
 * scanning enabled, Spring will automatically import the beans into the
 * container and inject to dependencies. These annotations are called Stereotype
 * annotations as well.
 * 
 * Annotation Meaning
 * 
 * @Component generic stereotype for any Spring-managed component
 * @Repository stereotype for persistence layer
 * @Service stereotype for service layer
 * @Controller stereotype for presentation layer (Spring-MVC)
 */
/**
 * REST layer for customers.
 */
@Controller
@Path("customers")
public class CustomerResource {

	private static final Logger LOG = LogManager.getLogger(CustomerResource.class);

	@Autowired
	private CustomerService customerService;

	/**
	 * Create a Customer.
	 *
	 * @param customer The customer to create.
	 * @param request  The HttpServletRequest used for the run-time caller resolution.
	 * @return <p>HTTP Status OK (200) the bean with all the fields including the id populated.</p>
	 * @throws BadRequestException if the validation of the Customer failed.
	 * @throws InternalServerErrorException if a server side error occurred.
	 */
	@POST
	@Consumes({ "application/xml", "application/json" })
	@Produces({ "application/xml", "application/json" })
	@TypeHint(Customer.class)
	public Response createCustomer(Customer customer, @Context HttpServletRequest request)
			throws BadRequestException, InternalServerErrorException {
		try {
			customerService.createCustomer(customer);
			return Response.ok(customer).build(); // Returns a 200 OK with the customer.
		} catch (ValidationException e) {
			ErrorMessage message = new ErrorMessage(Response.Status.BAD_REQUEST.getStatusCode(),
					e.getValidationErrors().stream().map(ve -> ve.toString()).collect(Collectors.joining(",")),
					"http://localhost:8080/error400.jsp", Response.Status.BAD_REQUEST.getReasonPhrase());
			LOG.warn(message);
			throw new BadRequestException(message, request.getHeader("accept"));
		} catch (Exception e) {
			ErrorMessage message = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage(), "http://localhost:8080/error500.jsp",
					Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase());
			LOG.error(message, e);
			throw new InternalServerErrorException(message, request.getHeader("accept"));
		}
	}

	/**
	 * Update a Customer.
	 *
	 * @param id       The id of the customer to update.
	 * @param customer The customer to update.
	 * @param request  The HttpServletRequest used for the run-time caller
	 *                 resolution.
	 * @return <p>HTTP Status No Content (204)</p>
	 * @throws BadRequestException if the validation of the Customer failed.
	 * @throws InternalServerErrorException if a server side error occurred.
	 */
	@PUT
	@Path("{id}")
	@Consumes({ "application/xml", "application/json" })
	@TypeHint(Customer.class)
	public Response updateCustomer(@PathParam("id") int id, Customer customer, @Context HttpServletRequest request)
			throws BadRequestException, InternalServerErrorException {
		try {
			customerService.updateCustomer(id, customer);
			return Response.noContent().build(); // Returns a 204 No Content - The server processed the request successfully, but is not returning any content.
		} catch (ValidationException e) {
			ErrorMessage message = new ErrorMessage(Response.Status.BAD_REQUEST.getStatusCode(),
					e.getValidationErrors().stream().map(ve -> ve.toString()).collect(Collectors.joining(",")),
					"http://localhost:8080/error400.jsp", Response.Status.BAD_REQUEST.getReasonPhrase());
			LOG.warn(message);
			throw new BadRequestException(message, request.getHeader("accept"));
		} catch (Exception e) {
			ErrorMessage message = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage(), "http://localhost:8080/error500.jsp",
					Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase());
			LOG.error(message, e);
			throw new InternalServerErrorException(message, request.getHeader("accept"));
		}
	}

	/**
	 * Delete a Customer.
	 *
	 * @param id      The id of the customer to delete.
	 * @param request The HttpServletRequest used for the run-time caller resolution.
	 * @return<p>HTTP Status No Content (204)</p>
	 * @throws InternalServerErrorException if a server side error occurred.
	 */
	@DELETE
	@Path("{id}")
	public Response deleteCustomer(@PathParam("id") int id, @Context HttpServletRequest request)
			throws InternalServerErrorException {
		try {
			customerService.deleteCustomer(id);
			return Response.noContent().build(); // Returns a 204 No Content - The server processed the request successfully, but is not returning any content.
		} catch (Exception e) {
			ErrorMessage message = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage(), "http://localhost:8080/error500.jsp",
					Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase());
			LOG.error(message, e);
			throw new InternalServerErrorException(message, request.getHeader("accept"));
		}
	}

	/**
	 * Retrieve a Customer.
	 *
	 * @param id      The id of the customer to retrieve.
	 * @param request The HttpServletRequest used for the run-time caller resolution.
	 * @return<p>HTTP Status OK (200) the Customer.</p>
	 * @throws NotFoundException if the Customer was not found.
	 * @throws InternalServerErrorException if a server side error occurred.
	 */
	@GET
	@Path("{id}")
	@Produces({ "application/xml", "application/json" })
	@TypeHint(Customer.class)
	public Response getCustomer(@PathParam("id") int id, @Context HttpServletRequest request)
			throws NotFoundException, InternalServerErrorException {
		Customer customer = null;
		try {
			customer = customerService.getCustomer(id);
		} catch (Exception e) {
			ErrorMessage message = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage(), "http://localhost:8080/error500.jsp",
					Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase());
			LOG.error(message, e);
			throw new InternalServerErrorException(message, request.getHeader("accept"));
		}

		if (customer == null) {
			ErrorMessage message = new ErrorMessage(Response.Status.NOT_FOUND.getStatusCode(), "Customer not found",
					"http://localhost:8080/error404.jsp", Response.Status.NOT_FOUND.getReasonPhrase());
			LOG.warn(message);
			throw new NotFoundException(message, request.getHeader("accept"));
		}

		// return customer; 
		// You can return a Customer object from this method but if you want to take advantage of HTTP caching
		// of objects you need to return a Response object.
		CacheControl cacheControl = new CacheControl();
		cacheControl.setMaxAge(120); // 2 minutes
		cacheControl.setPrivate(false);
		return Response.ok(customer).cacheControl(cacheControl).build(); // Returns a 200 OK with the customer.
	}

	/**
	 * Retrieve all the Customers.
	 *
	 * @return <p>HTTP Status OK (200) all the Customers.</p>
	 * @throws InternalServerErrorException if a server side error occurred.
	 */
	@GET
	@Produces({ "application/xml", "application/json" })
	@TypeHint(Customers.class)
	public Response getCustomers(@Context HttpServletRequest request) throws InternalServerErrorException {
		try {
			List<Customer> customers = customerService.getCustomers();
			// Wrap the list of customers in a Customers object for clients requesting XML
			// or JSON. This is primarily done for XML, for JSON you can just pass a List<Customer>
			// and it will convert it into an array. We wrap the JSON so that we don't have to have
			// additional methods that produce and consume Customers for XML and List<Customer> for JSON.
			Customers customersObject = new Customers();
			customersObject.setCustomers(customers);
			return Response.ok(customersObject).build(); // Returns a 200 OK with the customers object.
		} catch (Exception e) {
			ErrorMessage message = new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
					e.getMessage(), "http://localhost:8080/error500.jsp",
					Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase());
			LOG.error(message, e);
			throw new InternalServerErrorException(message, request.getHeader("accept"));
		}
	}
	
	/**
	 * Create Many Customers. Accepting and returning a List<Customer> only works
	 * for JSON. This does not work for XML, XML needs a wrapper object. The json
	 * looks proper, an array of customer.
	 * 
	 * [{"id":1,"first_name":"Nick","last_name":"Vujasin","email":"nick_vujasin@yahoo.com"},{"id":2,"first_name":"Stella","last_name":"Vujasin","email":"stella_vujasin@yahoo.com"}]
	 */
//	@POST
//	@Path("/createmany")
//	@Consumes({ "application/json" })
//	@Produces({ "application/json" })
//	@TypeHint(List.class)
//	public Response createCustomers(List<Customer> customers, @Context HttpServletRequest request)
//			throws BadRequestException, InternalServerErrorException {
//	}

	/**
	 * Create Many Customers. Accepting and returning a Customers wrapper object
	 * works for XML and JSON. JSON can also a wrapper object but it looks ugly. The
	 * array of customer is wrapped by an object.
	 * 
	 * {"customers":[{"id":1,"first_name":"Nick","last_name":"Vujasin","email":"nick_vujasin@yahoo.com"},{"id":2,"first_name":"Stella","last_name":"Vujasin","email":"stella_vujasin@yahoo.com"}]}
	 */
//	@POST
//	@Path("/createmany")
//	@Consumes({"application/xml", "application/json"})
//	@Produces({"application/xml", "application/json"})
//	@TypeHint(List.class)
//	public Response createCustomers(Customers customers, @Context HttpServletRequest request) 
//			throws BadRequestException, InternalServerErrorException {}

}
