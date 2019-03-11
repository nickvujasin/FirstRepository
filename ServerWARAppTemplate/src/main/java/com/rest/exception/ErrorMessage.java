package com.rest.exception;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "error_message")
@XmlType(propOrder = { "code", "description", "link", "status" })
public class ErrorMessage {

	private int code;
	private String description;
	private String link;
	private String status;
	
	// Required for JAXB marshalling and unmarshalling. Will never be invoked.
	@SuppressWarnings("unused")
	private ErrorMessage() {}
	
	public ErrorMessage(int code, String description, String link, String status) {
		super();
		this.code = code;
		this.description = description;
		this.link = link;
		this.status = status;
	}
	
	@XmlElement(name = "code")
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	
	@XmlElement(name = "description")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@XmlElement(name = "link")
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	
	@XmlElement(name = "status")
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		return new StringBuilder()
				.append("code: ").append(code)
				.append(", description: ").append(description)
				.append(", link: ").append(link)
				.append(", status: ").append(status)
				.toString();
	}
}
