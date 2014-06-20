package org.mqnaas.core.api.exceptions;

import org.mqnaas.core.api.ICapability;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class CapabilityNotFoundException extends Exception {

	/**
	 * Generated serial version UID
	 */
	private static final long	serialVersionUID	= -2428377962148756255L;

	private ICapability			capability;

	public CapabilityNotFoundException() {
		super();
	}

	public CapabilityNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public CapabilityNotFoundException(String message) {
		super(message);
	}

	public CapabilityNotFoundException(Throwable cause) {
		super(cause);
	}

	public CapabilityNotFoundException(ICapability capability) {
		super();
		this.setCapability(capability);
	}

	public CapabilityNotFoundException(ICapability capability, String message, Throwable cause) {
		super(message, cause);
		this.setCapability(capability);
	}

	public CapabilityNotFoundException(ICapability capability, String message) {
		super(message);
		this.setCapability(capability);
	}

	public CapabilityNotFoundException(ICapability capability, Throwable cause) {
		super(cause);
		this.setCapability(capability);
	}

	/**
	 * @return the capability
	 */
	public ICapability getCapability() {
		return capability;
	}

	/**
	 * @param capability
	 *            the capability to set
	 */
	public void setCapability(ICapability capability) {
		this.capability = capability;
	}
}
