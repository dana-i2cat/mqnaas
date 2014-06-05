package org.mqnaas.core.impl.exceptions;

import org.mqnaas.core.impl.ApplicationInstance;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class ApplicationInstanceNotFoundException extends Exception {

	/**
	 * Auto-generated UID for serialization
	 */
	private static final long	serialVersionUID	= -1836093779618611355L;

	ApplicationInstance			target;

	public ApplicationInstanceNotFoundException(ApplicationInstance target) {
		super();
		this.target = target;
	}

	public ApplicationInstanceNotFoundException(ApplicationInstance target, String message, Throwable cause) {
		super(message, cause);
		this.target = target;
	}

	public ApplicationInstanceNotFoundException(ApplicationInstance target, String message) {
		super(message);
		this.target = target;
	}

	public ApplicationInstanceNotFoundException(ApplicationInstance target, Throwable cause) {
		super(cause);
		this.target = target;
	}

	public ApplicationInstance getTarget() {
		return target;
	}

}
