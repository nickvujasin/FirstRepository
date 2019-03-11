package com.rest.domain;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Wrapper class that wraps a list of customers.
 * 
 * <customers> // <customers> is defined by the @XmlRootElement.
 * 		<customer>...</customer> // <customer> is defined by the @XmlElement.
 * 		<customer>...</customer>
 * </customers>
 * 
 * {"customers":[{...,...,...,..."},{...,...,...,..}]} // "customers" is defined by the @JsonProperty
 */
@XmlRootElement(name = "customers")
@XmlAccessorType(XmlAccessType.FIELD)
public class Customers {

	@XmlElement(name = "customer")
	@JsonProperty("customers") 
	private List<Customer> customers = new ArrayList<Customer>();

	public List<Customer> getCustomers() {
		return customers;
	}

	public void setCustomers(List<Customer> customers) {
		this.customers = customers;
	}

	public void add(Customer customers) {
		this.customers.add(customers);
	}
	
	@Override
	public String toString() {
		return customers.toString();
	}
}