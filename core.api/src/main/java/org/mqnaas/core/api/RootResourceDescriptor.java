package org.mqnaas.core.api;

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

@XmlRootElement(namespace = "org.mqnaas")
@XmlType(propOrder = { "specification", "lockingBehaviourClass", "transactionBehaviourClass", "endpoints" })
@XmlAccessorType(XmlAccessType.FIELD)
public class RootResourceDescriptor {

	private Class<? extends ITransactionBehavior>	transactionBehaviourClass;

	private Class<? extends ILockingBehaviour>		lockingBehaviourClass;

	@XmlElement(required = true)
	private Specification							specification;

	@XmlElementWrapper(name = "endpoints")
	@XmlElement(name = "endpoint")
	private Collection<Endpoint>					endpoints	= new ArrayList<Endpoint>();

	private RootResourceDescriptor() {
	}

	private RootResourceDescriptor(Specification specification, Collection<Endpoint> endpoints) {
		if (endpoints == null || endpoints.size() < 1) {
			if (!specification.getType().equals(Type.NETWORK))
				throw new IllegalArgumentException(
						"Invalid endpoint collection, at least one endpoint is required. Endpoints = " + endpoints);
		}

		this.specification = specification;
		this.endpoints = endpoints;
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

		sb.append("]");

		return sb.toString();
	}

	public static RootResourceDescriptor create(Specification specification) {
		return create(specification, null);
	}

	public static RootResourceDescriptor create(Specification specification, Collection<Endpoint> endpoints) {
		return new RootResourceDescriptor(specification, endpoints);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((endpoints == null) ? 0 : endpoints.hashCode());
		result = prime * result + ((lockingBehaviourClass == null) ? 0 : lockingBehaviourClass.getName().hashCode());
		result = prime * result + ((specification == null) ? 0 : specification.hashCode());
		result = prime * result + ((transactionBehaviourClass == null) ? 0 : transactionBehaviourClass.getName().hashCode());
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
		} else if (!transactionBehaviourClass
				.equals(other.transactionBehaviourClass))
			return false;
		return true;
	}
}
