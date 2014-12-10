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

	private String					id;

	// FIXME descriptor duplicates info in specification and endpoints.
	// This field is not used in equals and hashcode methods.
	private RootResourceDescriptor	descriptor;

	protected RootResource(RootResourceDescriptor descriptor) throws InstantiationException, IllegalAccessException {

		this.descriptor = descriptor;
		this.id = generateIdFromDescriptor(descriptor);

		// If the descriptor contains behavior, initialize them now...
		if (descriptor.getLockingBehaviourClass() != null) {
			this.lockingBehaviour = descriptor.getLockingBehaviourClass().newInstance();
		}

		if (descriptor.getTransactionBehaviourClass() != null) {
			this.transactionBehaviour = descriptor.getTransactionBehaviourClass().newInstance();
		}
	}

	// This constructor is only used by serialization machinery
	RootResource() {
	}

	@Override
	public String getId() {
		return id;
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
	public String toString() {
		Specification specification = descriptor.getSpecification();

		StringBuilder sb = new StringBuilder("Resource [");

		sb.append("type=").append(specification.getType());
		sb.append(", model=").append(specification.getModel());
		sb.append(", endpoints=").append(descriptor.getEndpoints());

		sb.append(" ]");

		return sb.toString();
	}

	// FIXME Generated ID is not unique! All resources with same specification will have same ID!!!
	private static String generateIdFromDescriptor(RootResourceDescriptor descriptor) {
		Specification specification = descriptor.getSpecification();

		StringBuilder sb = new StringBuilder(specification.getType().toString());

		if (!StringUtils.isEmpty(specification.getModel())) {
			sb.append("-").append(specification.getModel());
		}

		if (!StringUtils.isEmpty(specification.getVersion())) {
			sb.append("-").append(specification.getVersion());
		}

		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((descriptor == null) ? 0 : descriptor.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RootResource other = (RootResource) obj;
		if (descriptor == null) {
			if (other.descriptor != null)
				return false;
		} else if (!descriptor.equals(other.descriptor))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
