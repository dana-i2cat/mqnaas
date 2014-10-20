package org.mqnaas.core.impl;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

import org.mqnaas.core.api.Endpoint;
import org.mqnaas.core.api.ILockingBehaviour;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.ITransactionBehavior;
import org.mqnaas.core.api.Specification;

@XmlRootElement
public class RootResource implements IRootResource {

	private ITransactionBehavior	transactionBehaviour	= new UnawareTransactionBehaviour();

	private ILockingBehaviour		lockingBehaviour		= new DefaultLockingBehaviour();

	private Specification			specification;
	private Collection<Endpoint>	endpoints;

	// This constructor is only used by serialization machinery
	Resource() {
	}

	protected RootResource(Specification specification, Collection<Endpoint> endpoints) {
		this.specification = specification;
		this.endpoints = endpoints;
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
	public String getId() {
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
