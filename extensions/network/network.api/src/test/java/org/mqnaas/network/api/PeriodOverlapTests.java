package org.mqnaas.network.api;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.mqnaas.network.api.request.Period;

/**
 * {@link Period} tests regarding overlaps
 * 
 * @author Julio Carlos Barrera
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class PeriodOverlapTests {

	/**
	 * Test checks the {@link Period#overlap(Period)} method with different Period combinations.
	 */
	@Test
	public void periodsOverlapTest() {

		long currentTime = System.currentTimeMillis();

		// startDate2 < endDate2 < starDate1 < endDate1 -> FALSE
		Period period1 = new Period(new Date(currentTime), new Date(currentTime + 5000L));
		Period period2 = new Period(new Date(currentTime - 6000L), new Date(currentTime - 5000L));
		Assert.assertFalse(period1.overlap(period2));

		// startDate1 < endDate1 < starDate2 < endDate2 -> FALSE
		period1 = new Period(new Date(currentTime), new Date(currentTime + 5000L));
		period2 = new Period(new Date(currentTime + 10000L), new Date(currentTime + 15000L));
		Assert.assertFalse(period1.overlap(period2));

		// strartDate2 < startDate1 < endDate1 < endDate2 -> TRUE
		period1 = new Period(new Date(currentTime), new Date(currentTime + 15000L));
		period2 = new Period(new Date(currentTime + 5000L), new Date(currentTime + 10000L));
		Assert.assertTrue(period1.overlap(period2));

		// strartDate2 < startDate1 < endDate2 < endDate1 -> TRUE
		period1 = new Period(new Date(currentTime), new Date(currentTime + 10000L));
		period2 = new Period(new Date(currentTime + 5000L), new Date(currentTime + 15000L));
		Assert.assertTrue(period1.overlap(period2));

		// strartDate1 < startDate2 < endDate1 < endDate2 -> TRUE
		period1 = new Period(new Date(currentTime + 5000L), new Date(currentTime + 15000L));
		period2 = new Period(new Date(currentTime), new Date(currentTime + 10000L));
		Assert.assertTrue(period1.overlap(period2));

		// startDate1 = startDate2
		period1 = new Period(new Date(currentTime), new Date(currentTime + 15000L));
		period2 = new Period(new Date(currentTime), new Date(currentTime + 10000L));
		Assert.assertTrue(period1.overlap(period2));

		// endDate1 = endDate2
		period1 = new Period(new Date(currentTime + 5000), new Date(currentTime + 10000L));
		period2 = new Period(new Date(currentTime), new Date(currentTime + 10000L));
		Assert.assertTrue(period1.overlap(period2));

		// endDateX = startDateY
		period1 = new Period(new Date(currentTime), new Date(currentTime + 5000L));
		period2 = new Period(new Date(currentTime + 5000L), new Date(currentTime + 10000L));
		Assert.assertTrue(period1.overlap(period2));

	}
}
