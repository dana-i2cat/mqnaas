package org.mqnaas.core.api.slicing;

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

import org.mqnaas.core.api.ICapability;

/**
 * This capability allows to manage the additional attributes of the slice unit.
 * 
 * It is thought to be bound to units.
 * 
 * @author Georg Mansky-Kummert
 */
public interface IUnitAdministration extends ICapability {

	/**
	 * Initializes the {@link Range} for the given {@link Unit}, e.g. the width of the available slicing elements along that axis.
	 */
	void setRange(Range range);

	/**
	 * Returns the {@link Range} defined for the given slice {@link Unit}. If the give <code>unit</code> is not defined in this slice,
	 * <code>null</code> is returned.
	 */
	Range getRange();

}
