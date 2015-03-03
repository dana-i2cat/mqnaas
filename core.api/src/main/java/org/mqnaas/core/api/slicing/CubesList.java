package org.mqnaas.core.api.slicing;

/*
 * #%L
 * MQNaaS :: Core.API
 * %%
 * Copyright (C) 2007 - 2015 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

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
