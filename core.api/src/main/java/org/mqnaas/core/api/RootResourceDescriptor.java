package org.mqnaas.core.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.i2cat.utils.StringBuilderUtils;

@XmlRootElement(namespace = "org.mqnaas")
@XmlType(propOrder = { "specification", "lockingBehaviour", "transactionBehaviour", "endpoints" })
public class RootResourceDescriptor {

	private Class<? extends ITransactionBehavior>	transactionBehaviourClass;

	private Class<? extends ILockingBehaviour>		lockingBehaviourClass;

	private Specification							specification;

	private Collection<Endpoint>					endpoints	= new ArrayList<Endpoint>();

	private RootResourceDescriptor() {
	}

	private RootResourceDescriptor(Specification specification, Collection<Endpoint> endpoints) {
		if (endpoints == null || endpoints.size() < 1) {
			throw new IllegalArgumentException("Invalid endpoint collection, at least one endpoint is required. Endpoints = " + endpoints);
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

	@XmlElement(required = true)
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

	// @XmlElementWrapper(name = "endpoints")
	// @XmlElement(name = "endpoint")
	public void setEndpoints(Collection<Endpoint> endpoints) {

		this.endpoints.clear();

		if (endpoints == null)
			return;

		for (Endpoint endpoint : endpoints)
			addEndpoint(endpoint);
	}

	public Collection<Endpoint> getEndpoints() {
		return endpoints.isEmpty() ? Collections.<Endpoint> emptyList() : new ArrayList<Endpoint>(endpoints);
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

		if (endpoints.isEmpty()) {
			sb.append(", endpoints=(");
			StringBuilderUtils.append(sb, endpoints);
			sb.append(")");
		} else {
			sb.append(", endpoints=none");
		}

		sb.append("]");

		return sb.toString();
	}

	public static RootResourceDescriptor create(Specification specification, Collection<Endpoint> endpoints) {
		return new RootResourceDescriptor(specification, endpoints);
	}
}
