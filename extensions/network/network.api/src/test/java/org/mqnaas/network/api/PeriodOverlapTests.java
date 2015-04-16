package org.mqnaas.network.api;

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
