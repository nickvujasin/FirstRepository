package com.example.bean;

// This message is defined in the my-beans.xml file.
public class Message {

	private String message;

	// The my-beans.xml file will inject the message string.
	public void setMessage(String message) {

		this.message = message;
	}

	public String getMessage() {

		return message;
	}
}
