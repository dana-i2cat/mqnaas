package org.mqnaas.network.api.reservation;

/*
 * #%L
 * MQNaaS :: Network API
 * %%
 * Copyright (C) 2007 - 2015 Fundació Privada i2CAT, Internet i
 * 			Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class ResourceReservationException extends Exception {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -8077229848023802861L;

	public ResourceReservationException() {
		super();
	}

	public ResourceReservationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ResourceReservationException(String message) {
		super(message);
	}

	public ResourceReservationException(Throwable cause) {
		super(cause);
	}

}
