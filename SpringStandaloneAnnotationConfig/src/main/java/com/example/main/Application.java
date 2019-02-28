package com.example.main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.example.bean.Message;

// The ApplicationContext is created from annotations.
@ComponentScan(basePackages = "com.example")
@Configuration // @Configuration is not required since it is assumed this is a @Configuration class by using the @ComponentScan.
			   // According to the Javadoc, the annotation @Configuraion indicates that a class declares one or more @Bean methods 
			   // and may be processed by the Spring container to generate bean definitions and service requests for those beans 
			   // at runtime. So it is like a factory class to produce some beans declared in it or through a component scan.
public class Application {

	public static void main(String[] args) {
		
		// Create a new AnnotationConfigApplicationContext, deriving bean definitions from the given 
		// annotated classes and automatically refreshing the context. The constructor of the 
		// AnnotationConfigApplicationContext class takes multiple arguments of the @Configuration class. 
		// That means all configuration could be passed at once during the creation of the context. That 
		// functionality enables adding separate configurations for separate components like databases 
		// or custom security features.
		ApplicationContext context = new AnnotationConfigApplicationContext(Application.class);
		
		Application p = context.getBean(Application.class);
		p.start();
		
		((AnnotationConfigApplicationContext) context).close();
	}
	
	@Autowired
	// Here we are autowiring (injecting) the Message bean.
    private Message message;
    
	private void start() {
        System.out.println("Message: " + message.getMessage());
    }
}
