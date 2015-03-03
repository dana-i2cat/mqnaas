package org.mqnaas.core.api.exceptions;

/*
 * #%L
 * MQNaaS :: Core.API
 * %%
 * Copyright (C) 2007 - 2015 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

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
