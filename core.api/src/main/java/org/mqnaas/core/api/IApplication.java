package org.mqnaas.core.api;

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

import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;

/**
 * <p>
 * <code>IApplication</code> is a marker interface to identify an MQNaaS application. Applications will be instantiated automatically by the platform,
 * therefore they need to have a constructor without arguments.
 * <p>
 * <p>
 * Applications may depend on capabilities which will be injected attributes annotated with {@link DependingOn}.
 * </p>
 */
public interface IApplication {

	/**
	 * Performs initialization required in order to have all its services available.
	 * 
	 * It is guaranteed that this method is called only when all dependencies (attributes annotated with {@link DependingOn}) have been injected.
	 * 
	 * The use of services provided by dependencies inside this method requires some care. In case of dependency cycles, it cannot be guaranteed that
	 * dependencies would have been activated before this method is called.
	 */
	void activate() throws ApplicationActivationException;

	/**
	 * Cleans initialization done in {@link IApplication#activate()}
	 * 
	 * Nothing can be granted regarding the availability of dependencies when this method is called. Dependencies may not be functional any more.
	 */
	void deactivate();

}
