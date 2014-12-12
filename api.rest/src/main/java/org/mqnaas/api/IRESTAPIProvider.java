package org.mqnaas.api;

import org.mqnaas.core.api.ICapability;

/**
 * Core capability offering publication of services defined in a capability in the REST API.
 * 
 * @author Georg Mansky-Kummert (i2CAT)
 * 
 */
public interface IRESTAPIProvider extends ICapability {

	void publish(ICapability capability, Class<? extends ICapability> interfaceToPublish, String uri) throws Exception;

	void unpublish(ICapability capability, Class<? extends ICapability> interfaceToUnPublish) throws Exception;
}
