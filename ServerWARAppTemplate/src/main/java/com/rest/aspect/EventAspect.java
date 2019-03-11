package com.rest.aspect;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.RedirectionException;
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
			Object value = pjp.proceed();

			if (value instanceof Response) {
				event.success((Response) value);
			}
			
			return value;
		}
		catch (RedirectionException e)
        {
			event.seeOther(e.getLocation());
			throw e;
        }
        catch (Exception e)
        {
        	if (e instanceof WebApplicationException) {
        		event.failure(((WebApplicationException) e).getResponse(), e);
        	} else {
        		event.failure(e);       
        	}
        	
        	throw e;
        	
        } finally {
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
