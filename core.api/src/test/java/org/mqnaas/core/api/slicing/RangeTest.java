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

import org.junit.Assert;
import org.junit.Test;

/**
 * <p>
 * Class containing tests for {@link Range} creation.
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class RangeTest {

	/**
	 * Test checks the range's lower bound could not be greater than the upper bound.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void invalidBoundTest() {
		new Range(4, 2);
	}

	/**
	 * Test checks the range is successfully created if lower bound is not greater than the uppter bound.
	 */
	@Test
	public void rangeCreationTest() {

		Range range = new Range(0, 0);
		Assert.assertTrue(range.getLowerBound() == 0 && range.getUpperBound() == 0);

		range = new Range(-4, 5);
		Assert.assertTrue(range.getLowerBound() == -4 && range.getUpperBound() == 5);

	}

}
