package com.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * The context root is provided in the web.xml as <url-pattern>/rest/*</url-pattern>
 * To reach this HelloResource resource you need to point your browser to http://localhost:8080/rest/hello
 */
@Path("hello")
public class HelloResource {

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String sayHtmlHello() {
		return "<h1>Hello from REST</h1>";
	}
}
