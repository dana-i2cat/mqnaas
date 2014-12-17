package org.mqnaas.core.impl.slicing;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;

import org.mqnaas.core.api.IResource;

/**
 * 
 * <p>
 * A SliceUnit represents one dimension of the multi-dimensional space offered by the slice. Each of these units has a name and a type.
 * </p>
 * 
 * @author Georg Mansky-Kummert (i2CAT)
 * @author Adrián Roselló Rey (i2CAT)
 * 
 */
@XmlRootElement(namespace = "org.mqnaas", name = "unit")
@XmlAccessorType(XmlAccessType.FIELD)
public class UnitResource implements IResource, Serializable {

	private static final long		serialVersionUID	= -731594872847650988L;

	private static AtomicInteger	ID_COUNTER			= new AtomicInteger();

	private String					id;

	@XmlEnum
	public enum Type {
		DISCRETE;
	}

	private String	name;
	private Type	type;

	// no-arg constructor for JAXB
	UnitResource() {
	}

	public UnitResource(String name) {
		this.name = name;
		this.type = Type.DISCRETE;

		id = "unit-" + ID_COUNTER.incrementAndGet();
	}

	public String getName() {
		return name;
	}

	public Type getType() {
		return type;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UnitResource other = (UnitResource) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SliceUnit [name=" + name + ", type=" + type + "]";
	}

}
