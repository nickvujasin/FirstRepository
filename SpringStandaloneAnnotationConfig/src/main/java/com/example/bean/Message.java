package com.example.bean;

import org.springframework.stereotype.Component;

// Using @Component is like defining this class in a Spring configuration xml file. 
// Using @Component will be auto-detected by Spring, you just need to tell Spring which directories to scan. 
@Component
public class Message {

	private String message = "Hello there!";

	public void setMessage(String message) {

		this.message = message;
	}

	public String getMessage() {

		return message;
	}
}