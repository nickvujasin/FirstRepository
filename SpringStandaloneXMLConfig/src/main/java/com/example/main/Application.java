package com.example.main;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.example.bean.Message;

// The ApplicationContext is created from a xml configuration file.
public class Application {

	public static void main(String[] args) {
		
		// Create a new ClassPathXmlApplicationContext, loading the definitions from the given 
		// XML file and automatically refreshing the context. The constructor of the 
		// ClassPathXmlApplicationContext class takes multiple arguments of config locations. 
		// That means all configuration files could be passed at once during the creation of the context. That 
		// functionality enables adding separate configurations for separate components like databases 
		// or custom security features.
		ApplicationContext context = new ClassPathXmlApplicationContext("my-beans.xml");

		// Retrieve the Message bean from the my-beans.xml context file.
		Message obj = (Message) context.getBean("mymessage");

		String msg = obj.getMessage();
		System.out.println(msg);
	}
}
