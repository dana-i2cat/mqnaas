package org.mqnaas.bundletree.exceptions;

/*
 * #%L
 * MQNaaS :: BundleTree
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

/**
 * Bundle Not Found Exception
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class BundleNotFoundException extends Exception {

	private static final long	serialVersionUID	= -2583413201025561370L;

	public BundleNotFoundException() {
		super();
	}

	public BundleNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public BundleNotFoundException(String message) {
		super(message);
	}

	public BundleNotFoundException(Throwable cause) {
		super(cause);
	}

}
