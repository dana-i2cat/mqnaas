package org.mqnaas.network.api.request;

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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.commons.lang3.StringUtils;

/**
 * {@link XmlAdapter} for MQNaaS periods, responsible of transformations between {@link Date}s and {@link String}s representing UNIX timestamps.
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class DateAdapter extends XmlAdapter<String, Date> {

	/**
	 * Builds a {@link Date} object from the given UNIX timestamp.
	 */
	@Override
	public Date unmarshal(String v) throws Exception {
		if (!StringUtils.isNumeric(v))
			throw new IllegalArgumentException("Wrong date format. UNIX timestamp expected.");

		return new Date(Long.valueOf(v) * 1000);
	}

	/**
	 * Transforms the given Date into UNIX timestamp.
	 */
	@Override
	public String marshal(Date v) throws Exception {

		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(v);

		return String.valueOf(calendar.getTimeInMillis() / 1000);

	}

}
