package org.mqnaas.test.helpers.resource;

import java.util.Collection;

import org.mqnaas.core.api.Endpoint;
import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.ILockingBehaviour;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.ITransactionBehavior;
import org.mqnaas.core.api.Specification;

/**
 * {@link IResource} factory able to generate artificial resources.
 * 
 * @author Julio Carlos Barrera
 *
 */
public class TestResourceFactory {

	/**
	 * Generates an artificial {@link IRootResource} with given parameters. This resource would be not present in any {@link ICapability}.
	 * 
	 * @param transactionBehavior
	 * @param specification
	 * @param lockingBehaviour
	 * @param endpoints
	 * @return generated resource
	 */
	public static IRootResource createIRootResource(final ITransactionBehavior transactionBehavior, final Specification specification,
			final ILockingBehaviour lockingBehaviour, final Collection<Endpoint> endpoints) {

		return new IRootResource() {

			@Override
			public ITransactionBehavior getTransactionBehaviour() {
				return transactionBehavior;
			}

			@Override
			public Specification getSpecification() {
				return specification;
			}

			@Override
			public ILockingBehaviour getLockingBehaviour() {
				return lockingBehaviour;
			}

			@Override
			public Collection<Endpoint> getEndpoints() {
				return endpoints;
			}
		};
	}
}
