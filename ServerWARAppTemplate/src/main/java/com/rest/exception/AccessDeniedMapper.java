package com.rest.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.springframework.security.access.AccessDeniedException;

/**
 * AccessDeniedMapper is instantiated by Jersey directly through the "jersey.config.server.provider.packages" setting.
 * 
 * Using global-method-security, Access Denied errors are returned as HTTP 500 errors. This issue is unrelated to 
 * Spring Security. The problem is with Jersey. Jersey is intercepting the AccessDeniedException and re-throwing it 
 * as a ServletException.
 */
@Provider
public class AccessDeniedMapper implements ExceptionMapper<AccessDeniedException> {
    @Override
    public Response toResponse(AccessDeniedException e) {
        return Response.status(401)
                .build();
    }
}