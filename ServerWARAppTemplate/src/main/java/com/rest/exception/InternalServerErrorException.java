package com.rest.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class InternalServerErrorException extends WebApplicationException {

	private static final long serialVersionUID = -6594608708756636812L;

	/**
	 * Create a HTTP 500 (Internal Server Error) Exception.
	 * @param message the String that is the entity of the 500 response.
	 * @param mediaType the requested media type by the client to format the exception in.  
	 * Defaults to Json if <code>null</code> or if an unknown media type was passed in.
	 * The supported media types are: <code>application/xml</code> and <code>application/json</code>
	 */
	public InternalServerErrorException(ErrorMessage message, String mediaType) {
		super(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				.entity(message)
				.type(mediaType != null ? 
						(mediaType.contains(MediaType.APPLICATION_XML) ? MediaType.APPLICATION_XML : MediaType.APPLICATION_JSON) 
						: MediaType.APPLICATION_JSON)
				.build());
	}
}
