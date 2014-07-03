package org.mqnaas.core.api.exceptions;

import org.mqnaas.core.api.IApplication;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class ApplicationNotFoundException extends Exception {

	/**
	 * Generated serial version UID
	 */
	private static final long	serialVersionUID	= -2428377962148756255L;

	private IApplication		application;

	public ApplicationNotFoundException() {
		super();
	}

	public ApplicationNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ApplicationNotFoundException(String message) {
		super(message);
	}

	public ApplicationNotFoundException(Throwable cause) {
		super(cause);
	}

	public ApplicationNotFoundException(IApplication application) {
		super();
		this.setApplication(application);
	}

	public ApplicationNotFoundException(IApplication application, String message, Throwable cause) {
		super(message, cause);
		this.setApplication(application);
	}

	public ApplicationNotFoundException(IApplication application, String message) {
		super(message);
		this.setApplication(application);
	}

	public ApplicationNotFoundException(IApplication application, Throwable cause) {
		super(cause);
		this.setApplication(application);
	}

	/**
	 * @return the capability
	 */
	public IApplication getApplication() {
		return application;
	}

	/**
	 * @param capability
	 *            the capability to set
	 */
	public void setApplication(IApplication application) {
		this.application = application;
	}
}
