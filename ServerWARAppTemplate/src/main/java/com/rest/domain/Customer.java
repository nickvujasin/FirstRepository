package com.rest.domain;

import java.util.Objects;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;


/**
 * Customer
 */
// @XmlRootElement: the name of the root XML element is derived from the class name 
// and we can also specify the name of the root element of the XML using its name attribute.
@XmlRootElement(name="customer")
// Setting the accessor type to the FIELD value and annotating our XML/JSON XmlElements on
// the fields allows us to use Reflection when validation is performed to find the names 
// of the XmlElements so we can return them instead of the object field names.
@XmlAccessorType(XmlAccessType.FIELD)
// @XmlType: define the order in which the fields are written in the XML/JSON response.
@XmlType(propOrder = { "id", "firstName", "lastName", "email" })
// Persistence
@Entity
@Table(name="customers")
// L2 Cache - These next 2 annotations are used when using JPA or Hibernate.
@Cacheable // javax.persistence
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE) // org.hibernate
public class Customer extends BaseDomain {
	
	@XmlElement(name = "first_name")	// XML/JSON parsing
	@NotBlank(message = "First Name must be populated") // Validation
	@Column(name="first_name")	// DB
	private String firstName;
	
	@XmlElement(name="last_name")		// XML/JSON parsing
	@NotBlank(message = "Last Name must be populated") // Validation
	@Column(name="last_name")	// DB
	private String lastName;
	
	@XmlElement(name="email")			// XML/JSON parsing
	@NotBlank(message = "Email must be populated") @Email(message = "Email must be valid") // Validation
	@Column(name="email")		// DB
	private String email;
	
	// Required for @Entity Persistence and for JAXB marshalling and unmarshalling.
	Customer() {}
	
	/**
	 * Constructor in which all fields are required.
	 * @param firstName
	 * @param lastName
	 * @param email must be unique.
	 */
	public Customer(String firstName, String lastName, String email ) {
		setFirstName(firstName);
		setLastName(lastName);
		setEmail(email);
	}
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = Objects.requireNonNull(firstName, "First name is required.");
	}
	
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = Objects.requireNonNull(lastName, "Last name is required.");
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = Objects.requireNonNull(email, "Email is required.").toLowerCase();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof Customer))
			return false;
		Customer customer = (Customer) o;
		return customer.getEmail().equals(email);
	}
	
	@Override
	public int hashCode() {
		return email.hashCode();
	}
	
	@Override
	public String toString() {
		return new StringBuilder()
				.append("id: ").append(getId())
				.append(", first name: ").append(firstName)
				.append(", last name: ").append(lastName)
				.append(", email: ").append(email)
				.toString();
	}
}
