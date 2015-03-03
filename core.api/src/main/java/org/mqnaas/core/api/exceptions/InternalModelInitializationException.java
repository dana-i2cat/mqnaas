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
 * <p>
 * Exception to be thrown by services initializing the internal model of a {@link IApplication}.
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class InternalModelInitializationException extends Exception {

	private static final long	serialVersionUID	= 1902148520708811010L;

	public InternalModelInitializationException() {
		super();
	}

	public InternalModelInitializationException(String message, Throwable cause) {
		super(message, cause);
	}

	public InternalModelInitializationException(String message) {
		super(message);
	}

	public InternalModelInitializationException(Throwable cause) {
		super(cause);
	}

}
