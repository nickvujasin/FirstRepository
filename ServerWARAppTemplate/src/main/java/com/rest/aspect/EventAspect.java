package com.rest.aspect;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;

import com.rest.jms.producer.JmsProducer;

/**
 * Aspect for generating Events for each REST method. These
 * methods must have a HttpServletRequest argument.
 */
@Aspect
public class EventAspect {

	private static final Logger LOG = LogManager.getLogger(EventAspect.class);
	
	@Autowired
	private JmsProducer jmsProducer;
	
	/**
	 * Around advice for REST method entry points.
	 *
	 * @param pjp The join point
	 * @return The return value of the REST method
	 * @throws Throwable
	 */
	@Around("execution(public * com.rest.resource.*Resource.*(..))")
	public Object doBasicProfiling(ProceedingJoinPoint pjp) throws Throwable {
		
		HttpServletRequest request = getRequest(pjp.getArgs());
		
		if (request == null) {
			LOG.error("No HttpServletRequest defined in method: {}", getMethodName(pjp));
			return pjp.proceed();
		}
		
		Event event = new Event(request).start();
		
		try {
			// Call the REST end point and wait for the REST end point's response.
			Object value = pjp.proceed();

			if (value instanceof Response) {
				// If there wasn't an exception then the REST call was a success.
				// Capture the details here.
				event.success((Response) value);
			}
			// Continue to return the response returned by the REST end point.
			return value;
			
        } catch (Exception e) {
        	// WebApplicationException is the super class exception of our REST exceptions. If
        	// the REST end point threw an exception capture the details here.
        	if (e instanceof WebApplicationException) {
        		event.failure(((WebApplicationException) e).getResponse(), e);
        	} else {
        		event.failure(e);       
        	}
        	// Continue to throw the exception thrown by the REST end point to the client.
        	throw e;
        	
        } finally {
        	// Place the event on the queue to be processed.
        	jmsProducer.send(event);
        }
	}

	/**
	 * Returns the HttpServletRequest from the joined methods parameter list.
	 *
	 * @param args The method parameters
	 * @return The HttpServletRequest, or null
	 */
	private static HttpServletRequest getRequest(Object[] args) {
		if (args != null) {
			for (Object arg : args) {
				if (arg instanceof HttpServletRequest) {
					return (HttpServletRequest) arg;
				}
			}
		}

		return null;
	}
	
	/**
	 * Returns the method name on the joined method.
	 *	
	 * @param joinPoint The join point
	 * @return The method name.
	 */
   private static String getMethodName(ProceedingJoinPoint joinPoint)
   {
      MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
      Method targetMethod = methodSignature.getMethod();

      return targetMethod.getName();
   }
}
