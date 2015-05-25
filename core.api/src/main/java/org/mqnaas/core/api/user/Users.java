package org.mqnaas.core.api.user;

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

import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>
 * Wrapper class for the usernames of the MQNaaS users. This class is required until the API provides the ability to wrap a {@link List}
 * </p>
 * 
 * 
 * TODO remove and (replace it) once the API serialize lists.
 * 
 * @author Adrian Rosello Rey (i2CAT)
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Users {

	@XmlElement(name = "user")
	Set<String>	usernames;

	public Users() {

	}

	public Users(Set<String> users) {
		this.usernames = users;
	}

	public Set<String> getUser() {
		return usernames;
	}

	public void setUser(Set<String> user) {
		this.usernames = user;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((usernames == null) ? 0 : usernames.hashCode());
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
		Users other = (Users) obj;
		if (usernames == null) {
			if (other.usernames != null)
				return false;
		} else if (!usernames.equals(other.usernames))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Users [users=" + usernames + "]";
	}

}
