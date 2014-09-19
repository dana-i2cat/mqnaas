package org.mqnaas.core.impl;

import java.util.Collection;

import org.mqnaas.core.api.Endpoint;
import org.mqnaas.core.api.ILockingBehaviour;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.ITransactionBehavior;
import org.mqnaas.core.api.Specification;

public class RootResource implements IRootResource {

	private ITransactionBehavior	transactionBehaviour	= new UnawareTransactionBehaviour();

	private ILockingBehaviour		lockingBehaviour		= new DefaultLockingBehaviour();

	private Specification			specification;

	private Collection<Endpoint>	endpoints;

	private String					id;

	@Override
	public String getId() {
		return id;
	}

	protected RootResource(Specification specification, Collection<Endpoint> endpoints, String id) {
		this.specification = specification;
		this.endpoints = endpoints;
		this.id = id;
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
	public Collection<Endpoint> getEndpoints() {
		return endpoints;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((endpoints == null) ? 0 : endpoints.hashCode());
		result = prime * result + ((specification == null) ? 0 : specification.hashCode());
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
		if (endpoints == null) {
			if (other.endpoints != null)
				return false;
		} else if (!endpoints.equals(other.endpoints))
			return false;
		if (specification == null) {
			if (other.specification != null)
				return false;
		} else if (!specification.equals(other.specification))
			return false;
		return true;
	}

	@Override
	public String toString() {
		Specification specification = getSpecification();

		StringBuilder sb = new StringBuilder("Resource [");

		sb.append("type=").append(specification.getType());
		sb.append(", model=").append(specification.getModel());
		sb.append(", endpoints=").append(endpoints);

		sb.append(" ]");

		return sb.toString();
	}

}
