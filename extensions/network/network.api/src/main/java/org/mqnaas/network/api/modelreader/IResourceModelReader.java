package org.mqnaas.network.api.modelreader;

import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IResource;

/**
 * <p>
 * Capability returning the model of a {@link IResource}.
 * </p>
 * <p>
 * The model of an <code>IResource</code> is defined and distributed among its {@link ICapability capabilities}. The goal of this capability is to
 * offer a general view of the resource state, by getting the information of all the capabilities of the resource.
 * </p>
 * 
 * @author Adrián Roselló Rey
 *
 */
public interface IResourceModelReader extends ICapability {

	public ResourceModelWrapper getResourceModel();

}
