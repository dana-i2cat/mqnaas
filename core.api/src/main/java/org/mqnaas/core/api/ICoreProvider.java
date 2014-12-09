package org.mqnaas.core.api;

import org.mqnaas.core.api.Specification.Type;

/**
 * <p>
 * This capability provides access to the {@link IRootResource} of type {@link Type#CORE}.
 * </p>
 * <p>
 * It should be bound to the core resource.
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public interface ICoreProvider {

	/**
	 * Returns the instance of the core {@link IRootResource}.
	 * 
	 * @return The <code>IRootResource</code> of type Core.
	 */
	public IRootResource getCore();

}
