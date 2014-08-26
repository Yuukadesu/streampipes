package de.fzi.cep.sepa.model.impl;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

import de.fzi.cep.sepa.model.UnnamedSEPAElement;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
@RdfsClass("sepa:StaticProperty")
@MappedSuperclass
@Entity
public abstract class StaticProperty extends UnnamedSEPAElement {

	@RdfProperty("sepa:hasName")
	String name;
	
	@RdfProperty("sepa:hasDescription")
	String description;
	
	
	public StaticProperty()
	{
		super();
	}
	
	public StaticProperty(String name, String description)
	{
		super();
		this.name = name;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
