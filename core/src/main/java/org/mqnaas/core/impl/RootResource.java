package org.mqnaas.core.impl;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;
import org.mqnaas.core.api.ILockingBehaviour;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.ITransactionBehavior;
import org.mqnaas.core.api.RootResourceDescriptor;
import org.mqnaas.core.api.Specification;

@XmlRootElement
public class RootResource implements IRootResource {

	private ITransactionBehavior	transactionBehaviour	= new UnawareTransactionBehaviour();

	private ILockingBehaviour		lockingBehaviour		= new DefaultLockingBehaviour();

	private RootResourceDescriptor	descriptor;

	// This constructor is only used by serialization machinery
	RootResource() {
	}

	public RootResource(RootResourceDescriptor descriptor) throws InstantiationException, IllegalAccessException {
		this.descriptor = descriptor;

		// If the descriptor contains behavior, initialize them now...
		if (descriptor.getLockingBehaviourClass() != null) {
			this.lockingBehaviour = descriptor.getLockingBehaviourClass().newInstance();
		}

		if (descriptor.getTransactionBehaviourClass() != null) {
			this.transactionBehaviour = descriptor.getTransactionBehaviourClass().newInstance();
		}
	}

	@Override
	public ITransactionBehavior getTransactionBehaviour() {
		return transactionBehaviour;
	}

	@Override
	public ILockingBehaviour getLockingBehaviour() {
		return lockingBehaviour;
	}

	@Override
	public RootResourceDescriptor getDescriptor() {
		return descriptor;
	}

	@Override
	public String getId() {
		Specification specification = descriptor.getSpecification();

		StringBuilder sb = new StringBuilder(specification.getType().toString());

		if (!StringUtils.isEmpty(specification.getModel())) {
			sb.append(":").append(specification.getModel());
		}

		if (!StringUtils.isEmpty(specification.getVersion())) {
			sb.append(":").append(specification.getVersion());
		}

		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((descriptor.getEndpoints() == null) ? 0 : descriptor.getEndpoints().hashCode());
		result = prime * result + ((descriptor.getSpecification() == null) ? 0 : descriptor.getSpecification().hashCode());
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
		RootResource other = (RootResource) obj;
		if (descriptor.getEndpoints() == null) {
			if (other.descriptor.getEndpoints() != null)
				return false;
		} else if (!descriptor.getEndpoints().equals(other.descriptor.getEndpoints()))
			return false;
		if (descriptor.getSpecification() == null) {
			if (other.descriptor.getSpecification() != null)
				return false;
		} else if (!descriptor.getSpecification().equals(other.descriptor.getSpecification()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		Specification specification = descriptor.getSpecification();

		StringBuilder sb = new StringBuilder("Resource [");
		sb.append("specification=").append(specification);

		sb.append(", endpoints=");
		if (descriptor.getEndpoints().isEmpty()) {
			sb.append("none");
		} else {
			sb.append(descriptor.getEndpoints());
		}

		sb.append(" ]");

		return sb.toString();
	}

}
