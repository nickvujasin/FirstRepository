package com.rest.aspect;

import java.io.Serializable;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

public class Event implements Serializable {

	private static final long serialVersionUID = -4522651373312208028L;

	private long startTime;
	private long duration;

	private String method;
	private String accept;
	private String resourcePath;
	private String resourceQueryParams;
	private String resourceURI;
	private String remoteAddress;
	private int status;
	private String message;

	public Event(HttpServletRequest request) {
		Objects.requireNonNull(request);

		method = request.getMethod();
		accept = request.getHeader("Accept");
		resourcePath = request.getPathInfo();
		resourceQueryParams = request.getQueryString();
		resourceURI = resourceQueryParams != null ? resourcePath + "?" + resourceQueryParams : resourcePath;
		remoteAddress = request.getRemoteAddr();
	}

	public Event start() {
		startTime = System.currentTimeMillis();
		return this;
	}

	public Event success(Response response) {
		status = response.getStatus();
		duration = System.currentTimeMillis() - startTime;
		return this;
	}

	public Event failure(Response response, Exception e) {
		status = response.getStatus();
		message = e.getLocalizedMessage();
		duration = System.currentTimeMillis() - startTime;
		return this;
	}

	public Event failure(Exception e) {
		status = 500;
		message = e.getLocalizedMessage();
		duration = System.currentTimeMillis() - startTime;
		return this;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getDuration() {
		return duration;
	}

	public String getMethod() {
		return method;
	}

	public String getAccept() {
		return accept;
	}

	public String getResourcePath() {
		return resourcePath;
	}

	public String getResourceQueryParams() {
		return resourceQueryParams;
	}

	public String getResourceURI() {
		return resourceURI;
	}

	public String getRemoteAddress() {
		return remoteAddress;
	}

	public int getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return "Event [startTime=" + startTime + ", duration=" + duration + ", method=" + method + ", accept=" + accept
				+ ", resourcePath=" + resourcePath + ", resourceQueryParams=" + resourceQueryParams + ", resourceURI="
				+ resourceURI + ", remoteAddress=" + remoteAddress + ", status=" + status + ", message=" + message
				+ "]";
	}
}