package org.mqnaas.clientprovider.api.client;

import org.mqnaas.core.api.IResource;

/**
 * TODO Javadoc
 * 
 * @author Georg Mansky-Kummert (i2CAT)
 */
public interface IClientProvider<T, CC> {

	T getClient(IResource resource);

	T getClient(IResource resource, CC clientConfiguration);

}
