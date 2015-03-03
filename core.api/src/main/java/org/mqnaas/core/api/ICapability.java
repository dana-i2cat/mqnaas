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

/**
 * <p>
 * <code>ICapability</code> is a marker interface to identify capability interfaces and their implementations.
 * </p>
 * 
 * <p>
 * Like {@link IApplication}s, <code>ICapability</code>s can depend on other capabilities to provide their services, which may be expressed by
 * annotating attributes with the {@link DependingOn} annotation. Services provided by a given capability implementation will only get available if
 * all capability dependencies are resolved.
 * </p>
 * 
 * TODO Define a mechanism that can be used to initialize capabilities after dependency resolution (see {@link IApplication} for another use-case of
 * this initialization mechanism).
 */
public interface ICapability extends IApplication {
}
