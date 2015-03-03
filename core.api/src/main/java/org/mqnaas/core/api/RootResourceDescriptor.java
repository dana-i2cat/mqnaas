package org.mqnaas.core.api;

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
import java.util.Collections;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.i2cat.utils.StringBuilderUtils;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.credentials.Credentials;

@XmlRootElement(namespace = "org.mqnaas")
@XmlType(propOrder = { "specification", "lockingBehaviourClass", "transactionBehaviourClass", "endpoints", "credentials" })
@XmlAccessorType(XmlAccessType.FIELD)
public class RootResourceDescriptor {

	private Class<? extends ITransactionBehavior>	transactionBehaviourClass;

	private Class<? extends ILockingBehaviour>		lockingBehaviourClass;

	@XmlElement(required = true)
	private Specification							specification;

	@XmlElementWrapper(name = "endpoints")
	@XmlElement(name = "endpoint")
	private Collection<Endpoint>					endpoints	= new ArrayList<Endpoint>();

	private Credentials								credentials;

	private RootResourceDescriptor() {
	}

	private RootResourceDescriptor(Specification specification, Collection<Endpoint> endpoints, Credentials credentials) {
		if (endpoints == null || endpoints.size() < 1) {
			if (!specification.getType().equals(Type.NETWORK) && !(specification.getType().equals(Type.CORE)))
				throw new IllegalArgumentException(
						"Invalid endpoint collection, at least one endpoint is required. Endpoints = " + endpoints);
		}

		if (specification == null || specification.getType() == null)
			throw new NullPointerException("RootResourceDescriptors require Specification with valid Type.");

		this.specification = specification;
		this.endpoints = endpoints;
		this.credentials = credentials;
	}

	public Class<? extends ITransactionBehavior> getTransactionBehaviourClass() {
		return transactionBehaviourClass;
	}

	public void setTransactionBehaviourClass(Class<? extends ITransactionBehavior> transactionBehaviourClass) {
		this.transactionBehaviourClass = transactionBehaviourClass;
	}

	public Class<? extends ILockingBehaviour> getLockingBehaviourClass() {
		return lockingBehaviourClass;
	}

	public void setLockingBehaviourClass(Class<? extends ILockingBehaviour> lockingBehaviourClass) {
		this.lockingBehaviourClass = lockingBehaviourClass;
	}

	public Specification getSpecification() {
		return specification;
	}

	public void setSpecification(Specification specification) {
		this.specification = specification;
	}

	public void addEndpoint(Endpoint endpoint) {
		if (endpoint == null)
			throw new NullPointerException("Endpoint must be given");

		endpoints.add(endpoint);
	}

	public boolean removeEndpoint(Endpoint endpoint) {
		return endpoints.remove(endpoint);
	}

	public void setEndpoints(Collection<Endpoint> endpoints) {

		this.endpoints.clear();

		if (endpoints == null)
			return;

		for (Endpoint endpoint : endpoints)
			addEndpoint(endpoint);
	}

	public Collection<Endpoint> getEndpoints() {
		return (endpoints == null || endpoints.isEmpty()) ? Collections.<Endpoint> emptyList() : new ArrayList<Endpoint>(endpoints);
	}

	public Credentials getCredentials() {
		return credentials;
	}

	public void setCredentials(Credentials credentials) {
		this.credentials = credentials;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("RRD [");
		sb.append("specification=").append(specification);

		if (transactionBehaviourClass != null) {
			sb.append(", transactionBehavior=").append(transactionBehaviourClass.getName());
		}

		if (lockingBehaviourClass != null) {
			sb.append(", lockingBehavior=").append(lockingBehaviourClass.getName());
		}

		if (!endpoints.isEmpty()) {
			sb.append(", endpoints=(");
			StringBuilderUtils.append(sb, endpoints);
			sb.append(")");
		} else {
			sb.append(", endpoints=none");
		}

		if (credentials != null)
			sb.append(", credentials=(").append(credentials).append(")");
		else
			sb.append(", credentials=none");

		sb.append("]");

		return sb.toString();
	}

	public static RootResourceDescriptor create(Specification specification) {
		return create(specification, null, null);
	}

	public static RootResourceDescriptor create(Specification specification, Collection<Endpoint> endpoints) {
		return create(specification, endpoints, null);
	}

	public static RootResourceDescriptor create(Specification specification, Collection<Endpoint> endpoints, Credentials credentials) {
		return new RootResourceDescriptor(specification, endpoints, credentials);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((credentials == null) ? 0 : credentials.hashCode());
		result = prime * result + ((endpoints == null) ? 0 : endpoints.hashCode());
		result = prime * result + ((lockingBehaviourClass == null) ? 0 : lockingBehaviourClass.hashCode());
		result = prime * result + ((specification == null) ? 0 : specification.hashCode());
		result = prime * result + ((transactionBehaviourClass == null) ? 0 : transactionBehaviourClass.hashCode());
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
		RootResourceDescriptor other = (RootResourceDescriptor) obj;
		if (credentials == null) {
			if (other.credentials != null)
				return false;
		} else if (!credentials.equals(other.credentials))
			return false;
		if (endpoints == null) {
			if (other.endpoints != null)
				return false;
		} else if (!endpoints.equals(other.endpoints))
			return false;
		if (lockingBehaviourClass == null) {
			if (other.lockingBehaviourClass != null)
				return false;
		} else if (!lockingBehaviourClass.equals(other.lockingBehaviourClass))
			return false;
		if (specification == null) {
			if (other.specification != null)
				return false;
		} else if (!specification.equals(other.specification))
			return false;
		if (transactionBehaviourClass == null) {
			if (other.transactionBehaviourClass != null)
				return false;
		} else if (!transactionBehaviourClass.equals(other.transactionBehaviourClass))
			return false;
		return true;
	}

}
