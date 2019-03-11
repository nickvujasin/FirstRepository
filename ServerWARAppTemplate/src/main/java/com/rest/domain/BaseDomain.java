package com.rest.domain;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.PositiveOrZero;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

// Since this parent class has been marked @XmlTransient the id property will be treated as
// part of the child classes when marshalling and unmarshalling into and out of XML and JSON.
@XmlTransient
@XmlAccessorType(XmlAccessType.FIELD)
// Persistence - Entities may inherit from super classes that contain persistent state and mapping information 
// but are not entities. That is, the superclass is not decorated with the @Entity annotation and is not mapped 
// as an entity by the Java Persistence provider. These super classes are most often used when you have state 
// and mapping information common to multiple entity classes. @MappedSuperclass designates a class whose mapping
// information is applied to the entities that inherit from it. A mapped superclass has no separate table defined for it.
@MappedSuperclass
public class BaseDomain {

	@XmlElement(name = "id") 	// XML/JSON parsing
	@PositiveOrZero(message = "Id must be 0 or greater") // Validation
	// Persistence annotations on the instance variables.
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
