package com.rest.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class NotFoundException extends WebApplicationException {

	private static final long serialVersionUID = -2917423809627951556L;

	/**
	 * Create a HTTP 404 (Not Found) Exception.
	 * @param message the String that is the entity of the 404 response.
	 * @param mediaType the requested media type to format the exception that will be returned. 
	 * Defaults to Json if <code>null</code> or if an unknown media type was passed in.
	 * The supported media types are: <code>application/xml</code> and <code>application/json</code>
	 */
	public NotFoundException(ErrorMessage message, String mediaType) {
		super(Response.status(Response.Status.NOT_FOUND)
				.entity(message)
				.type(mediaType != null ? 
						(mediaType.contains(MediaType.APPLICATION_XML) ? MediaType.APPLICATION_XML : MediaType.APPLICATION_JSON) 
						: MediaType.APPLICATION_JSON)
				.build());
	}
}
