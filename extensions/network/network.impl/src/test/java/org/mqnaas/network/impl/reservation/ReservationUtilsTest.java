package org.mqnaas.network.impl.reservation;

/*
 * #%L
 * MQNaaS :: Network Implementation
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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mqnaas.core.api.Endpoint;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.RootResourceDescriptor;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.impl.RootResource;
import org.mqnaas.network.api.request.Period;
import org.mqnaas.network.api.reservation.IReservationAdministration;

/**
 * <p>
 * Test for {@link ReservationUtils} class.
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class ReservationUtilsTest {

	private IRootResource	resource1;
	private IRootResource	resource2;

	@Before
	public void prepareTest() throws InstantiationException, IllegalAccessException, URISyntaxException {

		resource1 = new RootResource(RootResourceDescriptor.create(new Specification(Type.TSON),
				Arrays.asList(new Endpoint(new URI("http://localhost:8182")))));
		resource2 = new RootResource(RootResourceDescriptor.create(new Specification(Type.TSON),
				Arrays.asList(new Endpoint(new URI("http://localhost:8183")))));
	}

	/**
	 * Test checks the {@link ReservationUtils#periodsOverlap(Period, Period)} method with different periods combinations.
	 */
	@Test
	public void periodsOverlapTest() {

		long currentTime = System.currentTimeMillis();

		// startDate2 < endDate2 < starDate1 < endDate1 -> FALSE

		Period period1 = new Period(new Date(currentTime), new Date(currentTime + 5000L));
		Period period2 = new Period(new Date(currentTime - 6000L), new Date(currentTime - 5000L));
		Assert.assertFalse(ReservationUtils.periodsOverlap(period1, period2));

		// startDate1 < endDate1 < starDate2 < endDate2 -> FALSE

		period1 = new Period(new Date(currentTime), new Date(currentTime + 5000L));
		period2 = new Period(new Date(currentTime + 10000L), new Date(currentTime + 15000L));
		Assert.assertFalse(ReservationUtils.periodsOverlap(period1, period2));

		// strartDate2 < startDate1 < endDate1 < endDate2 -> TRUE

		period1 = new Period(new Date(currentTime), new Date(currentTime + 15000L));
		period2 = new Period(new Date(currentTime + 5000L), new Date(currentTime + 10000L));
		Assert.assertTrue(ReservationUtils.periodsOverlap(period1, period2));

		// strartDate2 < startDate1 < endDate2 < endDate1 -> TRUE

		period1 = new Period(new Date(currentTime), new Date(currentTime + 10000L));
		period2 = new Period(new Date(currentTime + 5000L), new Date(currentTime + 15000L));
		Assert.assertTrue(ReservationUtils.periodsOverlap(period1, period2));

		// strartDate1 < startDate2 < endDate1 < endDate2 -> TRUE

		period1 = new Period(new Date(currentTime + 5000L), new Date(currentTime + 15000L));
		period2 = new Period(new Date(currentTime), new Date(currentTime + 10000L));
		Assert.assertTrue(ReservationUtils.periodsOverlap(period1, period2));

		// startDate1 = startDate2
		period1 = new Period(new Date(currentTime), new Date(currentTime + 15000L));
		period2 = new Period(new Date(currentTime), new Date(currentTime + 10000L));
		Assert.assertTrue(ReservationUtils.periodsOverlap(period1, period2));

		// endDate1 = endDate2
		period1 = new Period(new Date(currentTime + 5000), new Date(currentTime + 10000L));
		period2 = new Period(new Date(currentTime), new Date(currentTime + 10000L));
		Assert.assertTrue(ReservationUtils.periodsOverlap(period1, period2));

		// endDateX = startDateY
		period1 = new Period(new Date(currentTime), new Date(currentTime + 5000L));
		period2 = new Period(new Date(currentTime + 5000L), new Date(currentTime + 10000L));
		Assert.assertTrue(ReservationUtils.periodsOverlap(period1, period2));

	}

	/**
	 * Test checks {@link ReservationUtils#areResourcesAvailable(IReservationAdministration, IReservationAdministration) method with different
	 * resources-periods combinations.}
	 */
	@Test
	public void areResourcesAvailableTest() {

		long currentTime = System.currentTimeMillis();

		IReservationAdministration existingReservationAdmin = new ReservationAdministration();
		IReservationAdministration reservationToCompareAdmin = new ReservationAdministration();

		// same devices, different periods
		Set<IRootResource> resources = new HashSet<IRootResource>();
		resources.add(resource1);
		resources.add(resource2);

		Period existingPeriod = new Period(new Date(currentTime), new Date(currentTime + 5000L));
		Period period = new Period(new Date(currentTime - 10000L), new Date(currentTime - 5000L));

		existingReservationAdmin.setPeriod(existingPeriod);
		existingReservationAdmin.setResources(resources);

		reservationToCompareAdmin.setPeriod(period);
		reservationToCompareAdmin.setResources(resources);

		Assert.assertTrue(ReservationUtils.areResourcesAvailable(existingReservationAdmin, reservationToCompareAdmin));

		// same devices, same period
		existingPeriod = new Period(new Date(currentTime), new Date(currentTime + 10000L));
		period = new Period(new Date(currentTime + 5000L), new Date(currentTime + 15000L));

		existingReservationAdmin.setPeriod(existingPeriod);
		reservationToCompareAdmin.setPeriod(period);

		Assert.assertFalse(ReservationUtils.areResourcesAvailable(existingReservationAdmin, reservationToCompareAdmin));

		// different devices, no matter period
		resources = new HashSet<IRootResource>();
		resources.add(resource1);
		existingPeriod = new Period(new Date(currentTime), new Date(currentTime + 5000L));
		existingReservationAdmin.setPeriod(existingPeriod);
		existingReservationAdmin.setResources(resources);

		resources = new HashSet<IRootResource>();
		resources.add(resource2);
		period = new Period(new Date(currentTime + 5000L), new Date(currentTime + 15000L));
		reservationToCompareAdmin.setPeriod(existingPeriod);
		reservationToCompareAdmin.setResources(resources);

		Assert.assertTrue(ReservationUtils.areResourcesAvailable(existingReservationAdmin, reservationToCompareAdmin));

	}
}
