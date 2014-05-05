package org.mqnaas.core.impl;

import org.mqnaas.core.api.ILockingBehaviour;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.ITransactionBehavior;
import org.mqnaas.core.api.Specification;

public class Resource implements IRootResource {

	private ITransactionBehavior	transactionBehaviour	= new UnawareTransactionBehaviour();

	private ILockingBehaviour		lockingBehaviour		= new DefaultLockingBehaviour();

	private Specification			specification;

	protected Resource(Specification specification) {
		this.specification = specification;
	}

	@Override
	public ITransactionBehavior getTransactionBehaviour() {
		return transactionBehaviour;
	}

	@Override
	public ILockingBehaviour getLockingBehaviour() {
		return lockingBehaviour;
	}

	public void setSpecification(Specification specification) {
		this.specification = specification;
	}

	@Override
	public Specification getSpecification() {
		return specification;
	}

	@Override
	public int hashCode() {
		return getSpecification().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Resource))
			return false;
		Resource other = (Resource) obj;
		return getSpecification().equals(other.getSpecification());
	}

	@Override
	public String toString() {
		Specification specification = getSpecification();

		StringBuilder sb = new StringBuilder("Resource [");

		sb.append("type=").append(specification.getType());
		sb.append(", model=").append(specification.getModel());

		sb.append("]");

		return sb.toString();
	}

}
