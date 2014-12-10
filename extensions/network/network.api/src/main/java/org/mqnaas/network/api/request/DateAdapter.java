package org.mqnaas.network.api.request;

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
