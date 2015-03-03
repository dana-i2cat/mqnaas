package org.i2cat.utils;

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

import java.util.Arrays;
import java.util.Collection;

public abstract class StringBuilderUtils {

	public interface ValueExtractor<T> {
		Object getValueToAppend(T value);
	}

	/**
	 * Extracts a {@link Class}'s name, e.g. {@link Class#getName()}
	 */
	public static ValueExtractor<Class<?>>	CLASSNAME_EXTRACTOR	= new ValueExtractor<Class<?>>() {
																	@Override
																	public Object getValueToAppend(Class<?> value) {
																		return value.getName();
																	}
																};

	private static String					DEFAULT_DELIMITER	= ", ";

	public static <T> StringBuilder create(T[] values) {
		return create(DEFAULT_DELIMITER, null, Arrays.asList(values));
	}

	public static <T> StringBuilder create(ValueExtractor<T> extractor, T[] values) {
		return create(DEFAULT_DELIMITER, extractor, Arrays.asList(values));
	}

	public static <T> StringBuilder create(String delimiter, T[] values) {
		return create(delimiter, null, values != null ? Arrays.asList(values) : null);
	}

	public static <T> StringBuilder create(String delimiter, Collection<T> values) {
		return create(delimiter, null, values);
	}

	public static <T> StringBuilder create(String delimiter, ValueExtractor<T> extractor, T[] values) {
		return create(delimiter, extractor, Arrays.asList(values));
	}

	public static <T> StringBuilder create(String delimiter, ValueExtractor<T> extractor, Collection<T> values) {
		StringBuilder sb = new StringBuilder();
		return append(sb, delimiter, extractor, values);
	}

	public static <T> StringBuilder append(StringBuilder sb, T[] values) {
		return append(sb, DEFAULT_DELIMITER, null, values);
	}

	public static <T> StringBuilder append(StringBuilder sb, Collection<T> values) {
		return append(sb, DEFAULT_DELIMITER, null, values);
	}

	public static <T> StringBuilder append(StringBuilder sb, ValueExtractor<T> extractor, T[] values) {
		return append(sb, DEFAULT_DELIMITER, extractor, values);
	}

	public static <T> StringBuilder append(StringBuilder sb, String delimiter, Collection<T> values) {
		return append(sb, delimiter, null, values);
	}

	public static <T> StringBuilder append(StringBuilder sb, String delimiter, T[] values) {
		return append(sb, delimiter, null, values);
	}

	public static <T> StringBuilder append(StringBuilder sb, String delimiter, ValueExtractor<T> extractor, T[] values) {
		return append(sb, delimiter, extractor, Arrays.asList(values));
	}

	public static <T> StringBuilder append(StringBuilder sb, String delimiter, ValueExtractor<T> extractor, Collection<T> values) {
		int i = 0;

		if (values != null) {
			for (T value : values) {
				if (i > 0) {
					sb.append(delimiter);
				}
				sb.append(extractor != null ? extractor.getValueToAppend(value) : value);
				i++;
			}
		}

		return sb;
	}

}
