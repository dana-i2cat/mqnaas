package org.mqnaas.api;

import org.mqnaas.api.exceptions.InvalidCapabilityDefinionException;
import org.mqnaas.core.api.ICapability;

/**
 * Core capability offering publication of services defined in a capability in the REST API.
 * 
 * @author Georg Mansky-Kummert (i2CAT)
 * 
 */
public interface IRESTAPIProvider extends ICapability {

	/**
	 * Publishes the given {@link ICapability} at the given <code>URI</code>. Since a capability implementation may provide the implementation for
	 * more than one capability interface, the <code>interfaceToPublish</code> defines, which of the implemented interfaces is published.
	 * 
	 * @param capability
	 *            The implementation used to back up the published interface.
	 * @param interfaceToPublish
	 *            The specific interface published
	 * @param URI
	 *            The URI used when publishing the interface
	 * @throws InvalidCapabilityDefinionException
	 *             Thrown when the given interface violates the publication restrictions.
	 */
	void publish(ICapability capability, Class<? extends ICapability> interfaceToPublish, String URI) throws InvalidCapabilityDefinionException;

	/**
	 * Unpublishes the already published <code>interfaceToUnPublish</code>.
	 * 
	 * @param capability
	 *            The implementation used to back up the published interface.
	 * @param interfaceToUnPublish
	 *            The specific interface to unpublish.
	 * @return <code>true</code>, if the unpublication was successful.
	 */
	boolean unpublish(ICapability capability, Class<? extends ICapability> interfaceToUnPublish);
}
