package org.mqnaas.core.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(propOrder = { "specification", "lockingBehaviour", "transactionBehaviour", "endpoints" })
public class RootResourceDescriptor {

	private Class<? extends ITransactionBehavior>	transactionBehaviour;

	private Class<? extends ILockingBehaviour>		lockingBehaviour;

	private Specification							specification;

	private Collection<Endpoint>					endpoints	= new ArrayList<Endpoint>();

	public RootResourceDescriptor() {
	}

	private RootResourceDescriptor(Specification specification) {
		this.specification = specification;
	}

	public Class<? extends ITransactionBehavior> getTransactionBehaviour() {
		return transactionBehaviour;
	}

	public void setTransactionBehaviour(Class<? extends ITransactionBehavior> transactionBehaviour) {
		this.transactionBehaviour = transactionBehaviour;
	}

	public Class<? extends ILockingBehaviour> getLockingBehaviour() {
		return lockingBehaviour;
	}

	public void setLockingBehaviour(Class<? extends ILockingBehaviour> lockingBehaviour) {
		this.lockingBehaviour = lockingBehaviour;
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

		for (Endpoint endpoint : endpoints) {
			sb.append(endpoint).append(" ");
		}

		return sb.toString();
	}

	public static RootResourceDescriptor create(Specification specification) {
		return new RootResourceDescriptor(specification);
	}
}
