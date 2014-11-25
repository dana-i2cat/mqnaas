package org.mqnaas.core.api;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "org.mqnaas")
@XmlAccessorType(XmlAccessType.FIELD)
public class RootResourceDescriptor {

	private Class<? extends ITransactionBehavior>	transactionBehaviour;

	private Class<? extends ILockingBehaviour>		lockingBehaviour;

	@XmlElement(required = true)
	private Specification							specification;

	private Collection<Endpoint>					endpoints;

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

	public Specification getSpecification() {
		return specification;
	}

	public void setSpecification(Specification specification) {
		this.specification = specification;
	}

	public Collection<Endpoint> getEndpoints() {
		return endpoints;
	}

	public void setEndpoints(Collection<Endpoint> endpoints) {
		this.endpoints = endpoints;
	}

	@Override
	public String toString() {
		return "RootResourceDescriptor [transactionBehaviour=" + transactionBehaviour + ", lockingBehaviour=" + lockingBehaviour + ", specification=" + specification + ", endpoints=" + endpoints + "]";
	}

}
