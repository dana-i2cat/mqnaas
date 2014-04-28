package org.opennaas.core.api;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RootResourceDescriptor {

	private Class<? extends ITransactionBehavior>	transactionBehaviour;

	private Class<? extends ILockingBehaviour>		lockingBehaviour;

	@XmlElement(required = true)
	private Specification							specification;

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

}
