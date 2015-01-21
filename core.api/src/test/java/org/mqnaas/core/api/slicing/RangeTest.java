package org.mqnaas.core.api.slicing;

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
