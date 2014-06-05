package org.mqnaas.core.impl.exceptions;

import org.mqnaas.core.impl.CapabilityInstance;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class CapabilityInstanceNotFoundException extends Exception {

	/**
	 * Auto-generated UID for serialization
	 */
	private static final long	serialVersionUID	= 6820983405484534893L;

	CapabilityInstance			target;

	public CapabilityInstanceNotFoundException(CapabilityInstance target) {
		super();
		this.target = target;
	}

	public CapabilityInstanceNotFoundException(CapabilityInstance target, String message, Throwable cause) {
		super(message, cause);
		this.target = target;
	}

	public CapabilityInstanceNotFoundException(CapabilityInstance target, String message) {
		super(message);
		this.target = target;
	}

	public CapabilityInstanceNotFoundException(CapabilityInstance target, Throwable cause) {
		super(cause);
		this.target = target;
	}

	public CapabilityInstance getTarget() {
		return target;
	}

}
