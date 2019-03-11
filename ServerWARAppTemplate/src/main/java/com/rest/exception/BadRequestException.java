package com.rest.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class BadRequestException extends WebApplicationException {

	private static final long serialVersionUID = -5789161480182210128L;

	/**
	 * Create a HTTP 400 (Bad Request) Exception.
	 * @param message the String that is the entity of the 400 response.
	 * @param mediaType the requested media type to format the exception that will be returned. 
	 * Defaults to Json if <code>null</code> or if an unknown media type was passed in.
	 * The supported media types are: <code>application/xml</code> and <code>application/json</code>
	 */
	public BadRequestException(ErrorMessage message, String mediaType) {
		super(Response.status(Response.Status.BAD_REQUEST)
				.entity(message)
				.type(mediaType != null ? 
						(mediaType.contains(MediaType.APPLICATION_XML) ? MediaType.APPLICATION_XML : MediaType.APPLICATION_JSON) 
						: MediaType.APPLICATION_JSON)
				.build());
	}
}
