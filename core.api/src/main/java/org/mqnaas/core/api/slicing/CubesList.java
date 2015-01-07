package org.mqnaas.core.api.slicing;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Temporary wrapper class to generate proper REST API
 * 
 * FIXME avoid using wrappers, use proper REST API implementation
 * 
 * @author Julio Carlos Barrera
 *
 */
@Deprecated
@XmlRootElement(namespace = "org.mqnaas")
@XmlAccessorType(XmlAccessType.NONE)
public class CubesList {

	@XmlElementWrapper(name = "cubes")
	@XmlElement(required = true, name = "cube")
	private Collection<Cube>	cubes;

	public CubesList() {
	}

	public CubesList(Collection<? extends Cube> c) {
		this.cubes = new ArrayList<Cube>(c);
	}

	public Collection<Cube> getCubes() {
		return cubes;
	}

	public void setCubes(Collection<Cube> cubes) {
		this.cubes = cubes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cubes == null) ? 0 : cubes.hashCode());
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
		CubesList other = (CubesList) obj;
		if (cubes == null) {
			if (other.cubes != null)
				return false;
		} else if (!cubes.equals(other.cubes))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CubesList [cubes=" + cubes + "]";
	}

}
